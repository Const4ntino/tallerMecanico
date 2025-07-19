package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.*;
import com.example.gestion.taller_mecanico.mapper.MantenimientoMapper;
import com.example.gestion.taller_mecanico.mapper.MantenimientoProductoMapper;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoRequest;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import com.example.gestion.taller_mecanico.model.entity.*;
import com.example.gestion.taller_mecanico.repository.*;
import com.example.gestion.taller_mecanico.specification.MantenimientoSpecification;
import com.example.gestion.taller_mecanico.utils.enums.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Join;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ServicioRepository servicioRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final ProductoRepository productoRepository;
    private final MantenimientoProductoRepository mantenimientoProductoRepository;
    private final ClienteRepository clienteRepository;
    private final MantenimientoMapper mantenimientoMapper;
    private final MantenimientoProductoMapper mantenimientoProductoMapper;
    private final AlertaRepository alertaRepository;
    private final FacturaRepository facturaRepository;

    @Override
    public List<MantenimientoResponse> findAll() {
        return mantenimientoRepository.findAll().stream()
                .map(this::mapMantenimientoToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MantenimientoResponse findById(Long id) {
        return mantenimientoRepository.findById(id)
                .map(this::mapMantenimientoToResponse)
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public MantenimientoResponse save(MantenimientoRequest mantenimientoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        Vehiculo vehiculo = vehiculoRepository.findById(mantenimientoRequest.getVehiculoId())
                .orElseThrow(() -> new VehiculoNotFoundException(
                        "Vehículo no encontrado con ID: " + mantenimientoRequest.getVehiculoId()));

        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(
                            () -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
            if (!vehiculo.getCliente().getId().equals(cliente.getId())) {
                throw new SecurityException("No tiene permiso para crear un mantenimiento para este vehículo.");
            }
            mantenimientoRequest.setEstado("SOLICITADO");
        }

        Servicio servicio = servicioRepository.findById(mantenimientoRequest.getServicioId())
                .orElseThrow(() -> new ServicioNotFoundException(
                        "Servicio no encontrado con ID: " + mantenimientoRequest.getServicioId()));

        Trabajador trabajador = null;
        if (mantenimientoRequest.getTrabajadorId() != null) {
            trabajador = trabajadorRepository.findById(mantenimientoRequest.getTrabajadorId())
                    .orElseThrow(() -> new TrabajadorNotFoundException(
                            "Trabajador no encontrado con ID: " + mantenimientoRequest.getTrabajadorId()));
        }

        Mantenimiento.MantenimientoBuilder builder = Mantenimiento.builder()
                .vehiculo(vehiculo)
                .servicio(servicio)
                .trabajador(trabajador)
                .estado(MantenimientoEstado.valueOf(mantenimientoRequest.getEstado().toUpperCase()))
                .observacionesCliente(mantenimientoRequest.getObservacionesCliente())
                .observacionesTrabajador(mantenimientoRequest.getObservacionesTrabajador());

        if (MantenimientoEstado
                .valueOf(mantenimientoRequest.getEstado().toUpperCase()) == MantenimientoEstado.EN_PROCESO) {
            vehiculo.setEstado(VehiculoEstado.EN_MANTENIMIENTO);
            Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);
            builder.fechaInicio(LocalDateTime.now());

            // 🚨 Crear alerta de mantenimiento en proceso
            Alerta alerta = new Alerta();
            alerta.setVehiculo(vehiculoActualizado);
            alerta.setCliente(vehiculoActualizado.getCliente());
            alerta.setTaller(servicio.getTaller()); // o donde obtengas el taller
            alerta.setTipo(AlertaTipo.MANTENIMIENTO_PREVENTIVO);
            alerta.setEstado(AlertaEstado.NUEVA);
            alerta.setMensaje("Tu vehículo con placa " + vehiculo.getPlaca() + " ha sido ingresado a mantenimiento. Te avisaremos cuando está listo a disponibilidad.");
            alertaRepository.save(alerta);

        } else if (MantenimientoEstado
                .valueOf(mantenimientoRequest.getEstado().toUpperCase()) == MantenimientoEstado.COMPLETADO) {
            vehiculo.setEstado(VehiculoEstado.ACTIVO);
            Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);
            builder.fechaFin(mantenimientoRequest.getFechaFin());
        } else  if (MantenimientoEstado
                .valueOf(mantenimientoRequest.getEstado().toUpperCase()) == MantenimientoEstado.PENDIENTE) {

            // 🚨 Crear alerta de mantenimiento en proceso
            Alerta alerta = new Alerta();
            alerta.setVehiculo(vehiculo);
            alerta.setCliente(vehiculo.getCliente());
            alerta.setTaller(servicio.getTaller()); // o donde obtengas el taller
            alerta.setTipo(AlertaTipo.NUEVA_SOLICITUD);
            alerta.setEstado(AlertaEstado.NUEVA);
            alerta.setMensaje("Ya puedes acercarte a nuestro taller para realizar el mantenimiento a tu vehículo con placa " + vehiculo.getPlaca() + "Por favor aproxímate a el taller " + servicio.getTaller().getNombre() + ": " + servicio.getTaller().getDireccion());
            alertaRepository.save(alerta);
        }

        Mantenimiento mantenimiento = builder.build();

        Mantenimiento savedMantenimiento = mantenimientoRepository.save(mantenimiento);

        if (mantenimientoRequest.getProductosUsados() != null && !mantenimientoRequest.getProductosUsados().isEmpty()) {
            for (var mpRequest : mantenimientoRequest.getProductosUsados()) {
                Producto producto = productoRepository.findById(mpRequest.getProductoId())
                        .orElseThrow(() -> new ProductoNotFoundException(
                                "Producto no encontrado con ID: " + mpRequest.getProductoId()));

                if (producto.getStock() < mpRequest.getCantidadUsada()) {
                    throw new InsufficientStockException(
                            "Stock insuficiente para el producto: " + producto.getNombre());
                }

                producto.setStock(producto.getStock() - mpRequest.getCantidadUsada());
                productoRepository.save(producto);

                MantenimientoProducto mantenimientoProducto = MantenimientoProducto.builder()
                        .id(new MantenimientoProductoId(savedMantenimiento.getId(), producto.getId()))
                        .mantenimiento(savedMantenimiento)
                        .producto(producto)
                        .cantidadUsada(mpRequest.getCantidadUsada())
                        .precioEnUso(mpRequest.getPrecioEnUso())
                        .build();
                mantenimientoProductoRepository.save(mantenimientoProducto);
            }
        }

        return mapMantenimientoToResponse(savedMantenimiento);
    }

    @Override
    @Transactional
    public MantenimientoResponse update(Long id, MantenimientoRequest mantenimientoRequest) {
        return mantenimientoRepository.findById(id)
                .map(mantenimiento -> {
                    // Devolver el stock de los productos eliminados o actualizados
                    List<MantenimientoProducto> productosAnteriores = mantenimientoProductoRepository.findAll()
                            .stream()
                            .filter(mp -> mp.getId().getMantenimientoId().equals(id))
                            .collect(Collectors.toList());

                    for (MantenimientoProducto mpAnterior : productosAnteriores) {
                        Producto producto = mpAnterior.getProducto();
                        producto.setStock(producto.getStock() + mpAnterior.getCantidadUsada());
                        productoRepository.save(producto);
                    }
                    mantenimientoProductoRepository.deleteAll(productosAnteriores);

                    Vehiculo vehiculo = vehiculoRepository.findById(mantenimientoRequest.getVehiculoId())
                            .orElseThrow(() -> new VehiculoNotFoundException(
                                    "Vehículo no encontrado con ID: " + mantenimientoRequest.getVehiculoId()));

                    Servicio servicio = servicioRepository.findById(mantenimientoRequest.getServicioId())
                            .orElseThrow(() -> new ServicioNotFoundException(
                                    "Servicio no encontrado con ID: " + mantenimientoRequest.getServicioId()));

                    Trabajador trabajador = null;
                    if (mantenimientoRequest.getTrabajadorId() != null) {
                        trabajador = trabajadorRepository.findById(mantenimientoRequest.getTrabajadorId())
                                .orElseThrow(() -> new TrabajadorNotFoundException(
                                        "Trabajador no encontrado con ID: " + mantenimientoRequest.getTrabajadorId()));
                    }

                    mantenimiento.setVehiculo(vehiculo);
                    mantenimiento.setServicio(servicio);
                    mantenimiento.setTrabajador(trabajador);
                    mantenimiento
                            .setEstado(MantenimientoEstado.valueOf(mantenimientoRequest.getEstado().toUpperCase()));
                    if (MantenimientoEstado
                            .valueOf(
                                    mantenimientoRequest.getEstado().toUpperCase()) == MantenimientoEstado.EN_PROCESO) {
                        vehiculo.setEstado(VehiculoEstado.EN_MANTENIMIENTO);
                        Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);
                        mantenimiento.setFechaInicio(LocalDateTime.now());

                        // 🚨 Crear alerta de mantenimiento en proceso
                        Alerta alerta = new Alerta();
                        alerta.setVehiculo(vehiculoActualizado);
                        alerta.setCliente(vehiculoActualizado.getCliente());
                        alerta.setTaller(servicio.getTaller()); // o donde obtengas el taller
                        alerta.setTipo(AlertaTipo.MANTENIMIENTO_PREVENTIVO);
                        alerta.setEstado(AlertaEstado.NUEVA);
                        alerta.setMensaje("Tu vehículo con placa " + vehiculo.getPlaca() + " ha sido ingresado a mantenimiento. Te avisaremos cuando está listo a disponibilidad.");
                        alertaRepository.save(alerta);

                    } else if (MantenimientoEstado
                            .valueOf(
                                    mantenimientoRequest.getEstado().toUpperCase()) == MantenimientoEstado.COMPLETADO
                            || MantenimientoEstado
                                    .valueOf(
                                            mantenimientoRequest.getEstado()
                                                    .toUpperCase()) == MantenimientoEstado.CANCELADO) {
                        vehiculo.setEstado(VehiculoEstado.ACTIVO);
                        Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);
                        mantenimiento.setFechaFin(LocalDateTime.now());

                        // 🚨 Crear alerta de mantenimiento en proceso
                        Alerta alerta = new Alerta();
                        alerta.setVehiculo(vehiculoActualizado);
                        alerta.setCliente(vehiculoActualizado.getCliente());
                        alerta.setTaller(servicio.getTaller()); // o donde obtengas el taller
                        alerta.setTipo(AlertaTipo.VEHICULO_LISTO);
                        alerta.setEstado(AlertaEstado.NUEVA);
                        alerta.setMensaje("Tu vehículo con placa " + vehiculo.getPlaca() + " ha completado su mantenimiento. Por favor aproxímate a el taller " + servicio.getTaller().getNombre() + ": " + servicio.getTaller().getDireccion());
                        alertaRepository.save(alerta);

                    } else if (MantenimientoEstado
                            .valueOf(
                                    mantenimientoRequest.getEstado().toUpperCase()) == MantenimientoEstado.PENDIENTE) {

                        // 🚨 Crear alerta de mantenimiento en proceso
                        Alerta alerta = new Alerta();
                        alerta.setVehiculo(vehiculo);
                        alerta.setCliente(vehiculo.getCliente());
                        alerta.setTaller(servicio.getTaller()); // o donde obtengas el taller
                        alerta.setTipo(AlertaTipo.NUEVA_SOLICITUD);
                        alerta.setEstado(AlertaEstado.NUEVA);
                        alerta.setMensaje("Ya puedes acercarte a nuestro taller para realizar el mantenimiento a tu vehículo con placa " + vehiculo.getPlaca() + "Por favor aproxímate a el taller " + servicio.getTaller().getNombre() + ": " + servicio.getTaller().getDireccion());
                        alertaRepository.save(alerta);
                    }


                    mantenimiento.setObservacionesCliente(mantenimientoRequest.getObservacionesCliente());
                    mantenimiento.setObservacionesTrabajador(mantenimientoRequest.getObservacionesTrabajador());

                    Mantenimiento updatedMantenimiento = mantenimientoRepository.save(mantenimiento);

                    // Actualizar productos usados
                    if (mantenimientoRequest.getProductosUsados() != null
                            && !mantenimientoRequest.getProductosUsados().isEmpty()) {
                        for (var mpRequest : mantenimientoRequest.getProductosUsados()) {
                            Producto producto = productoRepository.findById(mpRequest.getProductoId())
                                    .orElseThrow(() -> new ProductoNotFoundException(
                                            "Producto no encontrado con ID: " + mpRequest.getProductoId()));

                            if (producto.getStock() < mpRequest.getCantidadUsada()) {
                                throw new InsufficientStockException(
                                        "Stock insuficiente para el producto: " + producto.getNombre());
                            }

                            producto.setStock(producto.getStock() - mpRequest.getCantidadUsada());
                            productoRepository.save(producto);

                            MantenimientoProducto mantenimientoProducto = MantenimientoProducto.builder()
                                    .id(new MantenimientoProductoId(updatedMantenimiento.getId(), producto.getId()))
                                    .mantenimiento(updatedMantenimiento)
                                    .producto(producto)
                                    .cantidadUsada(mpRequest.getCantidadUsada())
                                    .precioEnUso(mpRequest.getPrecioEnUso())
                                    .build();
                            mantenimientoProductoRepository.save(mantenimientoProducto);
                        }
                    }

                    return mapMantenimientoToResponse(updatedMantenimiento);
                }).orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!mantenimientoRepository.existsById(id)) {
            throw new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id);
        }
        mantenimientoRepository.deleteById(id);
    }

    @Override
    public Page<MantenimientoResponse> findMantenimientosByFilters(String search, Long vehiculoId, Long servicioId,
            Long trabajadorId, String estado, LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta,
            LocalDateTime fechaFinDesde, LocalDateTime fechaFinHasta, Boolean estaFacturado, Pageable pageable) {
        Specification<Mantenimiento> spec = MantenimientoSpecification.filterMantenimientos(search, vehiculoId,
                servicioId, trabajadorId, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta,
                null, estaFacturado);
        return mantenimientoRepository.findAll(spec, pageable).map(this::mapMantenimientoToResponse);
    }

    @Override
    public Page<MantenimientoResponse> findMyMantenimientosByFilters(String search, Long vehiculoId, String estado,
            LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde,
            LocalDateTime fechaFinHasta, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        Specification<Mantenimiento> spec = MantenimientoSpecification.filterMantenimientos(search, vehiculoId, null,
                null, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta, cliente.getId());
        return mantenimientoRepository.findAll(spec, pageable).map(this::mapMantenimientoToResponse);
    }

    @Override
    public Page<MantenimientoResponse> findMantenimientosByTallerId(Long tallerId, String search, Long vehiculoId,
            Long servicioId, Long trabajadorId, String estado, LocalDateTime fechaInicioDesde,
            LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde, LocalDateTime fechaFinHasta,
            Pageable pageable) {
        Specification<Mantenimiento> spec = MantenimientoSpecification.filterMantenimientos(search, vehiculoId,
                servicioId, trabajadorId, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta,
                null);
        // Add a predicate to filter by tallerId from the vehicle's assigned workshop
        spec = spec.and((root, query, criteriaBuilder) -> {
            Join<Mantenimiento, Vehiculo> vehiculoJoin = root.join("vehiculo");
            Join<Vehiculo, Cliente> clienteJoin = vehiculoJoin.join("cliente");
            return criteriaBuilder.equal(clienteJoin.get("tallerAsignado").get("id"), tallerId);
        });
        return mantenimientoRepository.findAll(spec, pageable).map(this::mapMantenimientoToResponse);
    }

    @Override
    public Page<MantenimientoResponse> findAssignedMantenimientos(String search, Long vehiculoId, String estado,
            LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde,
            LocalDateTime fechaFinHasta, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(
                        () -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));

        Specification<Mantenimiento> spec = MantenimientoSpecification.filterMantenimientos(search, vehiculoId, null,
                trabajador.getId(), estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta, null);
        return mantenimientoRepository.findAll(spec, pageable).map(this::mapMantenimientoToResponse);
    }

    private MantenimientoResponse mapMantenimientoToResponse(Mantenimiento mantenimiento) {
        MantenimientoResponse response = mantenimientoMapper.toMantenimientoResponse(mantenimiento);
        List<MantenimientoProducto> productosUsados = mantenimientoProductoRepository.findAll()
                .stream()
                .filter(mp -> mp.getId().getMantenimientoId().equals(mantenimiento.getId()))
                .collect(Collectors.toList());
        response.setProductosUsados(productosUsados.stream()
                .map(mantenimientoProductoMapper::toMantenimientoProductoResponse)
                .collect(Collectors.toList()));
                
        // Verificar si el mantenimiento está facturado
        boolean estaFacturado = facturaRepository.existsByMantenimientoId(mantenimiento.getId());
        response.setEstaFacturado(estaFacturado);
        
        return response;
    }
}