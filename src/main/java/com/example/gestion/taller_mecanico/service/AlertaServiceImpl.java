package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.AlertaNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.ClienteNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.VehiculoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.mapper.AlertaMapper;
import com.example.gestion.taller_mecanico.model.dto.AlertaEstadoRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaResponse;
import com.example.gestion.taller_mecanico.model.entity.Alerta;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.repository.AlertaRepository;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.VehiculoRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.utils.enums.AlertaEstado;
import com.example.gestion.taller_mecanico.utils.enums.AlertaTipo;
import com.example.gestion.taller_mecanico.utils.enums.Rol;
import com.example.gestion.taller_mecanico.specification.AlertaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaServiceImpl implements AlertaService {

    private final AlertaRepository alertaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final TallerRepository tallerRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final AlertaMapper alertaMapper;

    @Override
    public List<AlertaResponse> findAll() {
        return alertaRepository.findAll().stream()
                .map(alertaMapper::toAlertaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AlertaResponse findById(Long id) {
        return alertaRepository.findById(id)
                .map(alertaMapper::toAlertaResponse)
                .orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + id));
    }

    @Override
    public AlertaResponse save(AlertaRequest alertaRequest) {
        Vehiculo vehiculo = null;
        if (alertaRequest.getVehiculoId() != null) {
            vehiculo = vehiculoRepository.findById(alertaRequest.getVehiculoId())
                    .orElseThrow(() -> new VehiculoNotFoundException("Vehículo no encontrado con ID: " + alertaRequest.getVehiculoId()));
        }

        Cliente cliente = null;
        if (alertaRequest.getClienteId() != null) {
            cliente = clienteRepository.findById(alertaRequest.getClienteId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + alertaRequest.getClienteId()));
        }

        Taller taller = null;
        if (alertaRequest.getTallerId() != null) {
            taller = tallerRepository.findById(alertaRequest.getTallerId())
                    .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + alertaRequest.getTallerId()));
        }

        Alerta alerta = Alerta.builder()
                .vehiculo(vehiculo)
                .cliente(cliente)
                .taller(taller)
                .tipo(AlertaTipo.valueOf(alertaRequest.getTipo().toUpperCase()))
                .mensaje(alertaRequest.getMensaje())
                .estado(AlertaEstado.valueOf(alertaRequest.getEstado().toUpperCase()))
                .build();
        return alertaMapper.toAlertaResponse(alertaRepository.save(alerta));
    }

    @Override
    public AlertaResponse update(Long id, AlertaRequest alertaRequest) {
        return alertaRepository.findById(id)
                .map(alerta -> {
                    Vehiculo vehiculo = null;
                    if (alertaRequest.getVehiculoId() != null) {
                        vehiculo = vehiculoRepository.findById(alertaRequest.getVehiculoId())
                                .orElseThrow(() -> new VehiculoNotFoundException("Vehículo no encontrado con ID: " + alertaRequest.getVehiculoId()));
                    }

                    Cliente cliente = null;
                    if (alertaRequest.getClienteId() != null) {
                        cliente = clienteRepository.findById(alertaRequest.getClienteId())
                                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + alertaRequest.getClienteId()));
                    }

                    Taller taller = null;
                    if (alertaRequest.getTallerId() != null) {
                        taller = tallerRepository.findById(alertaRequest.getTallerId())
                                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + alertaRequest.getTallerId()));
                    }

                    alerta.setVehiculo(vehiculo);
                    alerta.setCliente(cliente);
                    alerta.setTaller(taller);
                    alerta.setTipo(AlertaTipo.valueOf(alertaRequest.getTipo().toUpperCase()));
                    alerta.setMensaje(alertaRequest.getMensaje());
                    alerta.setEstado(AlertaEstado.valueOf(alertaRequest.getEstado().toUpperCase()));
                    return alertaMapper.toAlertaResponse(alertaRepository.save(alerta));
                }).orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!alertaRepository.existsById(id)) {
            throw new AlertaNotFoundException("Alerta no encontrada con ID: " + id);
        }
        alertaRepository.deleteById(id);
    }

    @Override
    public AlertaResponse markAlertaAsViewed(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
            if (alerta.getCliente() == null || !alerta.getCliente().getId().equals(cliente.getId())) {
                throw new SecurityException("No tiene permiso para marcar esta alerta como vista.");
            }
        } else {
            throw new SecurityException("No tiene permiso para realizar esta acción.");
        }

        alerta.setEstado(AlertaEstado.VISTA);
        return alertaMapper.toAlertaResponse(alertaRepository.save(alerta));
    }

    @Override
    public AlertaResponse updateAlertaEstado(Long id, AlertaEstadoRequest alertaEstadoRequest) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Lógica de seguridad para actualizar el estado de la alerta
        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
            if (alerta.getCliente() == null || !alerta.getCliente().getId().equals(cliente.getId())) {
                throw new SecurityException("No tiene permiso para cambiar el estado de esta alerta.");
            }
        } else if (usuario.getRol() == Rol.ADMINISTRADOR_TALLER || usuario.getRol() == Rol.TRABAJADOR) {
            Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));
            if (alerta.getTaller() == null || !alerta.getTaller().getId().equals(trabajador.getTaller().getId())) {
                throw new SecurityException("No tiene permiso para cambiar el estado de esta alerta.");
            }
        }

        alerta.setEstado(AlertaEstado.valueOf(alertaEstadoRequest.getEstado().toUpperCase()));
        return alertaMapper.toAlertaResponse(alertaRepository.save(alerta));
    }

    @Override
    public Page<AlertaResponse> findAlertasByFilters(String search, Long vehiculoId, Long clienteId, Long tallerId, String tipo, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Specification<Alerta> spec = AlertaSpecification.filterAlertas(search, vehiculoId, clienteId, tallerId, tipo, estado, fechaCreacionDesde, fechaCreacionHasta);
        return alertaRepository.findAll(spec, pageable).map(alertaMapper::toAlertaResponse);
    }

    @Override
    public Page<AlertaResponse> findMyAlertasByFilters(String search, Long vehiculoId, String tipo, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

        Specification<Alerta> spec = AlertaSpecification.filterAlertas(search, vehiculoId, cliente.getId(), null, tipo, estado, fechaCreacionDesde, fechaCreacionHasta);
        return alertaRepository.findAll(spec, pageable).map(alertaMapper::toAlertaResponse);
    }

    @Override
    public Page<AlertaResponse> findAlertasByTallerId(Long tallerId, String search, Long vehiculoId, Long clienteId, String tipo, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));

        Long tallerAsignadoId = trabajador.getTaller().getId();

        Specification<Alerta> spec = AlertaSpecification.filterAlertas(search, vehiculoId, clienteId, tallerAsignadoId, tipo, estado, fechaCreacionDesde, fechaCreacionHasta);
        return alertaRepository.findAll(spec, pageable).map(alertaMapper::toAlertaResponse);
    }

    @Override
    public Page<AlertaResponse> findPendingAlerts(Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Specification<Alerta> spec = Specification.where(null);

        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
            spec = spec.and(AlertaSpecification.filterAlertas(null, null, cliente.getId(), null, null, AlertaEstado.NUEVA.name(), null, null));
        } else if (usuario.getRol() == Rol.ADMINISTRADOR_TALLER || usuario.getRol() == Rol.TRABAJADOR) {
            Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));
            spec = spec.and(AlertaSpecification.filterAlertas(null, null, null, trabajador.getTaller().getId(), null, AlertaEstado.NUEVA.name(), null, null));
        } else if (usuario.getRol() == Rol.ADMINISTRADOR) {
            spec = spec.and(AlertaSpecification.filterAlertas(null, null, null, null, null, AlertaEstado.NUEVA.name(), null, null));
        }

        return alertaRepository.findAll(spec, pageable).map(alertaMapper::toAlertaResponse);
    }
}
