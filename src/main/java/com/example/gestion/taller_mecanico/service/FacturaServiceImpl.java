package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.ClienteNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.FacturaNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.MantenimientoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.mapper.FacturaMapper;
import com.example.gestion.taller_mecanico.mapper.MantenimientoMapper;
import com.example.gestion.taller_mecanico.model.dto.CalculatedTotalResponse;
import com.example.gestion.taller_mecanico.model.dto.FacturaRequest;
import com.example.gestion.taller_mecanico.model.dto.FacturaResponse;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Factura;
import com.example.gestion.taller_mecanico.model.entity.Mantenimiento;
import com.example.gestion.taller_mecanico.model.entity.MantenimientoProducto;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.FacturaRepository;
import com.example.gestion.taller_mecanico.repository.MantenimientoProductoRepository;
import com.example.gestion.taller_mecanico.repository.MantenimientoRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.specification.FacturaSpecification;
import com.example.gestion.taller_mecanico.utils.enums.MantenimientoEstado;
import com.example.gestion.taller_mecanico.utils.enums.Rol;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.gestion.taller_mecanico.service.PdfGeneratorService;
import com.example.gestion.taller_mecanico.service.FileStorageService;
import com.example.gestion.taller_mecanico.utils.enums.Rol;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final MantenimientoProductoRepository mantenimientoProductoRepository;
    private final ClienteRepository clienteRepository;
    private final TallerRepository tallerRepository;
    private final FacturaMapper facturaMapper;
    private final MantenimientoMapper mantenimientoMapper;
    private final TrabajadorRepository trabajadorRepository;
    private final MantenimientoService mantenimientoService;
    private final PdfGeneratorService pdfGeneratorService;
    private final FileStorageService fileStorageService;

    @Override
    public List<FacturaResponse> findAll() {
        return facturaRepository.findAll().stream()
                .map(facturaMapper::toFacturaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FacturaResponse findById(Long id) {
        return facturaRepository.findById(id)
                .map(facturaMapper::toFacturaResponse)
                .orElseThrow(() -> new FacturaNotFoundException("Factura no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public FacturaResponse save(FacturaRequest facturaRequest) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(facturaRequest.getMantenimientoId())
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + facturaRequest.getMantenimientoId()));

        // Calcular el total
        BigDecimal precioBaseServicio = mantenimiento.getServicio().getPrecioBase();
        List<MantenimientoProducto> productosUsados = mantenimientoProductoRepository.findByMantenimientoId(mantenimiento.getId());
        BigDecimal totalProductosUsados = BigDecimal.ZERO;

        for (MantenimientoProducto mp : productosUsados) {
            BigDecimal costoProducto = mp.getPrecioEnUso().multiply(BigDecimal.valueOf(mp.getCantidadUsada()));
            totalProductosUsados = totalProductosUsados.add(costoProducto);
        }
        BigDecimal totalCalculado = precioBaseServicio.add(totalProductosUsados);

        Cliente cliente = clienteRepository.findById(facturaRequest.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + facturaRequest.getClienteId()));

        Taller taller = tallerRepository.findById(facturaRequest.getTallerId())
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + facturaRequest.getTallerId()));

        Factura factura = Factura.builder()
                .mantenimiento(mantenimiento)
                .cliente(cliente)
                .taller(taller)
                .total(totalCalculado) // Asignar el total calculado aquí
                .detalles(facturaRequest.getDetalles())
                .pdfUrl(null) // Inicialmente null, se actualizará después de generar el PDF
                .build();
        Factura savedFactura = facturaRepository.save(factura); // Guarda la factura inicialmente

        // --- Generación y Subida del PDF ---
        try {
            // 1. Generar el PDF
            byte[] pdfBytes = pdfGeneratorService.generateFacturaPdf(savedFactura, productosUsados); // Pasa la factura y los productos usados

            // 2. Subir el PDF y obtener la URL
            String pdfUrl = fileStorageService.storeFacturaPdf(pdfBytes, "factura_" + savedFactura.getId() + ".pdf");

            // 3. Actualizar la factura con la URL del PDF
            savedFactura.setPdfUrl(pdfUrl);
            facturaRepository.save(savedFactura); // Guarda la factura con la URL del PDF

        } catch (Exception e) {
            // Manejo de errores en la generación/subida del PDF
            System.err.println("Error al generar o subir el PDF de la factura: " + e.getMessage());
            // Aquí podrías lanzar una excepción personalizada o manejar el error de otra forma
            throw new RuntimeException("Error al generar o subir el PDF de la factura", e);
        }

        return facturaMapper.toFacturaResponse(savedFactura);
    }

    @Override
    @Transactional
    public FacturaResponse update(Long id, FacturaRequest facturaRequest) {
        return facturaRepository.findById(id)
                .map(facturaExistente -> {
                    Mantenimiento mantenimiento = mantenimientoRepository.findById(facturaRequest.getMantenimientoId())
                            .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + facturaRequest.getMantenimientoId()));

                    // Recalcular el total
                    BigDecimal precioBaseServicio = mantenimiento.getServicio().getPrecioBase();
                    List<MantenimientoProducto> productosUsados = mantenimientoProductoRepository.findByMantenimientoId(mantenimiento.getId());
                    BigDecimal totalProductosUsados = BigDecimal.ZERO;

                    for (MantenimientoProducto mp : productosUsados) {
                        BigDecimal costoProducto = mp.getPrecioEnUso().multiply(BigDecimal.valueOf(mp.getCantidadUsada()));
                        totalProductosUsados = totalProductosUsados.add(costoProducto);
                    }
                    BigDecimal totalCalculado = precioBaseServicio.add(totalProductosUsados);

                    Cliente cliente = clienteRepository.findById(facturaRequest.getClienteId())
                            .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + facturaRequest.getClienteId()));

                    Taller taller = tallerRepository.findById(facturaRequest.getTallerId())
                            .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + facturaRequest.getTallerId()));

                    facturaExistente.setMantenimiento(mantenimiento);
                    facturaExistente.setCliente(cliente);
                    facturaExistente.setTaller(taller);
                    facturaExistente.setTotal(totalCalculado); // Actualizar el total calculado
                    facturaExistente.setDetalles(facturaRequest.getDetalles());
                    // No actualizamos pdfUrl directamente desde el request, se regenera si es necesario

                    Factura updatedFactura = facturaRepository.save(facturaExistente);

                    // --- Regeneración y Subida del PDF (si es necesario) ---
                    try {
                        byte[] pdfBytes = pdfGeneratorService.generateFacturaPdf(updatedFactura, productosUsados);
                        String pdfUrl = fileStorageService.storeFacturaPdf(pdfBytes, "factura_" + updatedFactura.getId() + ".pdf");
                        updatedFactura.setPdfUrl(pdfUrl);
                        facturaRepository.save(updatedFactura);
                    } catch (Exception e) {
                        System.err.println("Error al regenerar o subir el PDF de la factura: " + e.getMessage());
                        throw new RuntimeException("Error al regenerar o subir el PDF de la factura", e);
                    }

                    return facturaMapper.toFacturaResponse(updatedFactura);
                }).orElseThrow(() -> new FacturaNotFoundException("Factura no encontrada con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!facturaRepository.existsById(id)) {
            throw new FacturaNotFoundException("Factura no encontrada con ID: " + id);
        }
        facturaRepository.deleteById(id);
    }

    @Override
    public FacturaResponse findFacturaDetailsById(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new FacturaNotFoundException("Factura no encontrada con ID: " + id));

        // Lógica de seguridad basada en el rol del usuario
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
            if (!factura.getCliente().getId().equals(cliente.getId())) {
                throw new SecurityException("No tiene permiso para ver los detalles de esta factura.");
            }
        } else if (usuario.getRol() == Rol.ADMINISTRADOR_TALLER) {
            Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));
            if (!factura.getTaller().getId().equals(trabajador.getTaller().getId())) {
                throw new SecurityException("No tiene permiso para ver los detalles de esta factura.");
            }
        } else if (usuario.getRol() == Rol.TRABAJADOR) {
            Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));
            if (!factura.getTaller().getId().equals(trabajador.getTaller().getId())) {
                throw new SecurityException("No tiene permiso para ver los detalles de esta factura.");
            }
        }

        // La FacturaResponse ya incluye el MantenimientoResponse anidado, que a su vez incluye los productos usados.
        // No es necesario cargar explícitamente los productos aquí, ya que el mapper se encarga de ello.
        return facturaMapper.toFacturaResponse(factura);
    }

    @Override
    public Page<FacturaResponse> findFacturasByFilters(String search, Long mantenimientoId, Long clienteId, Long tallerId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, Pageable pageable) {
        Specification<Factura> spec = FacturaSpecification.filterFacturas(search, mantenimientoId, clienteId, tallerId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal);
        return facturaRepository.findAll(spec, pageable).map(facturaMapper::toFacturaResponse);
    }

    @Override
    public Page<FacturaResponse> findMyFacturasByFilters(String search, Long mantenimientoId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        Specification<Factura> spec = FacturaSpecification.filterFacturas(search, mantenimientoId, cliente.getId(), null, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal);
        return facturaRepository.findAll(spec, pageable).map(facturaMapper::toFacturaResponse);
    }

    @Override
    public Page<FacturaResponse> findFacturasByTallerId(Long tallerId, String search, Long mantenimientoId, Long clienteId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));

        Long tallerAsignadoId = trabajador.getTaller().getId();

        Specification<Factura> spec = FacturaSpecification.filterFacturas(search, mantenimientoId, clienteId, tallerAsignadoId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal);
        return facturaRepository.findAll(spec, pageable).map(facturaMapper::toFacturaResponse);
    }

    @Override
    public Page<MantenimientoResponse> findCompletedAndUnbilledMantenimientos(Pageable pageable) {
        return mantenimientoRepository.findCompletedAndUnbilledMantenimientos(MantenimientoEstado.COMPLETADO, pageable)
                .map(mantenimientoMapper::toMantenimientoResponse); // Reutiliza el mapper de MantenimientoService
    }

    @Override
    public CalculatedTotalResponse calculateTotalForMantenimiento(Long mantenimientoId) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(mantenimientoId)
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + mantenimientoId));

        BigDecimal precioBaseServicio = mantenimiento.getServicio().getPrecioBase();
        List<MantenimientoProducto> productosUsados = mantenimientoProductoRepository.findByMantenimientoId(mantenimiento.getId());
        BigDecimal totalProductosUsados = BigDecimal.ZERO;

        for (MantenimientoProducto mp : productosUsados) {
            BigDecimal costoProducto = mp.getPrecioEnUso().multiply(BigDecimal.valueOf(mp.getCantidadUsada()));
            totalProductosUsados = totalProductosUsados.add(costoProducto);
        }
        BigDecimal totalCalculado = precioBaseServicio.add(totalProductosUsados);

        return CalculatedTotalResponse.builder()
                .mantenimientoId(mantenimientoId)
                .totalCalculado(totalCalculado)
                .build();
    }
}