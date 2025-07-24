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

import com.example.gestion.taller_mecanico.utils.enums.TipoFactura;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.gestion.taller_mecanico.utils.enums.MetodoPago;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final ArchivoService archivoService;

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
        return save(facturaRequest, null, true, null);
    }
    
    @Override
    @Transactional
    public FacturaResponse save(FacturaRequest facturaRequest, MultipartFile imagenOperacion, boolean conIgv, String ruc) {
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
        BigDecimal subtotal = precioBaseServicio.add(totalProductosUsados);
        
        // Calcular el total con IGV si corresponde
        BigDecimal totalCalculado;
        if (conIgv) {
            // Si incluye IGV, calculamos el 18% y lo sumamos al subtotal
            BigDecimal igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
            totalCalculado = subtotal.add(igv);
        } else {
            // Si no incluye IGV, el total es igual al subtotal
            totalCalculado = subtotal;
        }

        Cliente cliente = clienteRepository.findById(facturaRequest.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + facturaRequest.getClienteId()));

        Taller taller = tallerRepository.findById(facturaRequest.getTallerId())
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + facturaRequest.getTallerId()));

        // Procesar la imagen de operación si se proporciona
        String imagenOperacionUrl = null;
        if (imagenOperacion != null && !imagenOperacion.isEmpty()) {
            try {
                imagenOperacionUrl = archivoService.subirImagen(imagenOperacion);
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen de operación", e);
            }
        } else {
            // Si no hay imagen nueva pero hay URL en el request, mantener esa URL
            imagenOperacionUrl = facturaRequest.getImagenOperacion();
        }
        
        // Solo usar el RUC si el tipo de comprobante es FACTURA
        String rucToUse = null;
        if ("FACTURA".equals(facturaRequest.getTipo()) && ruc != null && !ruc.isEmpty()) {
            rucToUse = ruc;
        }
        
        Factura factura = Factura.builder()
                .mantenimiento(mantenimiento)
                .cliente(cliente)
                .taller(taller)
                .total(totalCalculado) // Asignar el total calculado con IGV si corresponde
                .detalles(facturaRequest.getDetalles())
                .pdfUrl(null) // Inicialmente null, se actualizará después de generar el PDF
                .metodoPago(Enum.valueOf(MetodoPago.class, facturaRequest.getMetodoPago()))
                .nroOperacion(facturaRequest.getNroOperacion())
                .imagenOperacion(imagenOperacionUrl)
                .tipo(Enum.valueOf(TipoFactura.class, facturaRequest.getTipo()))
                .build();
        
        // Guardar la factura inicialmente
        Factura savedFactura = facturaRepository.save(factura);
        
        // Recargar la factura desde la base de datos para obtener el código generado automáticamente
        // Esto asegura que tengamos todos los campos actualizados, incluido el código de factura
        final Long facturaId = savedFactura.getId(); // Crear una variable final para usar en la expresión lambda
        Factura facturaGuardada = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new FacturaNotFoundException("Factura no encontrada con ID: " + facturaId));
        FacturaResponse facturaResponse = facturaMapper.toFacturaResponse(factura);
        // Imprimir para depuración
        System.out.println("Id de factura generada" +  facturaId);
        System.out.println("Id de factura generada" +  facturaResponse.getId());
        System.out.println("Código de factura generado: " + facturaResponse.getCodigoFactura());

        // --- Generación y Subida del PDF ---
        try {
            // 1. Generar el PDF
            byte[] pdfBytes = pdfGeneratorService.generateFacturaPdf(savedFactura, productosUsados, conIgv, rucToUse); // Pasa la factura y los productos usados

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
        return update(id, facturaRequest, null, true, null);
    }
    
    @Override
    @Transactional
    public FacturaResponse update(Long id, FacturaRequest facturaRequest, MultipartFile imagenOperacion, boolean conIgv, String ruc) {
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
                    BigDecimal subtotal = precioBaseServicio.add(totalProductosUsados);
                    
                    // Calcular el total con IGV si corresponde
                    BigDecimal totalCalculado;
                    if (conIgv) {
                        // Si incluye IGV, calculamos el 18% y lo sumamos al subtotal
                        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
                        totalCalculado = subtotal.add(igv);
                    } else {
                        // Si no incluye IGV, el total es igual al subtotal
                        totalCalculado = subtotal;
                    }

                    Cliente cliente = clienteRepository.findById(facturaRequest.getClienteId())
                            .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + facturaRequest.getClienteId()));

                    Taller taller = tallerRepository.findById(facturaRequest.getTallerId())
                            .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + facturaRequest.getTallerId()));

                    // Procesar la imagen de operación si se proporciona
                    String imagenOperacionUrl = facturaExistente.getImagenOperacion(); // Mantener la URL existente por defecto
                    if (imagenOperacion != null && !imagenOperacion.isEmpty()) {
                        try {
                            imagenOperacionUrl = archivoService.subirImagen(imagenOperacion);
                        } catch (Exception e) {
                            throw new RuntimeException("Error al subir la imagen de operación", e);
                        }
                    } else if (facturaRequest.getImagenOperacion() != null) {
                        // Si no hay imagen nueva pero hay URL en el request, actualizar con esa URL
                        imagenOperacionUrl = facturaRequest.getImagenOperacion();
                    }
                    
                    // Solo usar el RUC si el tipo de comprobante es FACTURA
                    String rucToUse = null;
                    if ("FACTURA".equals(facturaRequest.getTipo()) && ruc != null && !ruc.isEmpty()) {
                        rucToUse = ruc;
                    }
                    
                    facturaExistente.setMantenimiento(mantenimiento);
                    facturaExistente.setCliente(cliente);
                    facturaExistente.setTaller(taller);
                    facturaExistente.setTotal(totalCalculado); // Actualizar el total calculado con IGV si corresponde
                    facturaExistente.setDetalles(facturaRequest.getDetalles());
                    // No actualizamos pdfUrl directamente desde el request, se regenera si es necesario
                    facturaExistente.setMetodoPago(Enum.valueOf(MetodoPago.class, facturaRequest.getMetodoPago()));
                    facturaExistente.setNroOperacion(facturaRequest.getNroOperacion());
                    facturaExistente.setImagenOperacion(imagenOperacionUrl);
                    facturaExistente.setTipo(Enum.valueOf(TipoFactura.class, facturaRequest.getTipo()));

                    // Guardar la factura actualizada
                    Factura updatedFactura = facturaRepository.save(facturaExistente);
                    
                    // Recargar la factura desde la base de datos para obtener el código generado automáticamente
                    // Esto asegura que tengamos todos los campos actualizados, incluido el código de factura
                    final Long facturaActualizadaId = updatedFactura.getId(); // Crear una variable final para usar en la expresión lambda
                    updatedFactura = facturaRepository.findById(facturaActualizadaId)
                            .orElseThrow(() -> new FacturaNotFoundException("No se encontró la factura con ID: " + facturaActualizadaId));
                    
                    // Imprimir para depuración
                    System.out.println("Código de factura actualizado: " + updatedFactura.getCodigoFactura());

                    // --- Regeneración y Subida del PDF (si es necesario) ---
                    try {
                        byte[] pdfBytes = pdfGeneratorService.generateFacturaPdf(updatedFactura, productosUsados, conIgv, rucToUse);
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
    public Page<FacturaResponse> findFacturasByFilters(String search, Long mantenimientoId, Long clienteId, Long tallerId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, MetodoPago metodoPago, Pageable pageable) {
        Specification<Factura> spec = FacturaSpecification.filterFacturas(search, mantenimientoId, clienteId, tallerId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal, metodoPago);
        return facturaRepository.findAll(spec, pageable).map(facturaMapper::toFacturaResponse);
    }

    @Override
    public Page<FacturaResponse> findMyFacturasByFilters(String search, Long mantenimientoId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, MetodoPago metodoPago, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        Specification<Factura> spec = FacturaSpecification.filterFacturas(search, mantenimientoId, cliente.getId(), null, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal, metodoPago);
        return facturaRepository.findAll(spec, pageable).map(facturaMapper::toFacturaResponse);
    }

    @Override
    public Page<FacturaResponse> findFacturasByTallerId(Long tallerId, String search, Long mantenimientoId, Long clienteId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, MetodoPago metodoPago, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));

        Long tallerAsignadoId = trabajador.getTaller().getId();

        Specification<Factura> spec = FacturaSpecification.filterFacturas(search, mantenimientoId, clienteId, tallerAsignadoId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal, metodoPago);
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