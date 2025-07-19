package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.ServicioNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.mapper.ServicioMapper;
import com.example.gestion.taller_mecanico.model.dto.ServicioRequest;
import com.example.gestion.taller_mecanico.model.dto.ServicioResponse;
import com.example.gestion.taller_mecanico.model.entity.Servicio;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Trabajador; // Importar Trabajador
import com.example.gestion.taller_mecanico.repository.ServicioRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.specification.ServicioSpecification;
import com.example.gestion.taller_mecanico.utils.enums.ServicioEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final TallerRepository tallerRepository;
    private final ServicioMapper servicioMapper;
    private final TrabajadorRepository trabajadorRepository;

    @Override
    public List<ServicioResponse> findAll() {
        return servicioRepository.findAll().stream()
                .map(servicioMapper::toServicioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServicioResponse findById(Long id) {
        return servicioRepository.findById(id)
                .map(servicioMapper::toServicioResponse)
                .orElseThrow(() -> new ServicioNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Override
    public ServicioResponse save(ServicioRequest servicioRequest) {
        Taller taller = tallerRepository.findById(servicioRequest.getTallerId())
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + servicioRequest.getTallerId()));

        Servicio servicio = Servicio.builder()
                .taller(taller)
                .nombre(servicioRequest.getNombre())
                .descripcion(servicioRequest.getDescripcion())
                .precioBase(servicioRequest.getPrecioBase())
                .estado(ServicioEstado.valueOf(servicioRequest.getEstado().toUpperCase()))
                .duracionEstimadaHoras(servicioRequest.getDuracionEstimadaHoras())
                .build();
        return servicioMapper.toServicioResponse(servicioRepository.save(servicio));
    }

    @Override
    public ServicioResponse update(Long id, ServicioRequest servicioRequest) {
        return servicioRepository.findById(id)
                .map(servicio -> {
                    Taller taller = tallerRepository.findById(servicioRequest.getTallerId())
                            .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + servicioRequest.getTallerId()));

                    servicio.setTaller(taller);
                    servicio.setNombre(servicioRequest.getNombre());
                    servicio.setDescripcion(servicioRequest.getDescripcion());
                    servicio.setPrecioBase(servicioRequest.getPrecioBase());
                    servicio.setEstado(ServicioEstado.valueOf(servicioRequest.getEstado().toUpperCase()));
                    servicio.setDuracionEstimadaHoras(servicioRequest.getDuracionEstimadaHoras());
                    return servicioMapper.toServicioResponse(servicioRepository.save(servicio));
                }).orElseThrow(() -> new ServicioNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new ServicioNotFoundException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    @Override
    public Page<ServicioResponse> findServiciosByFilters(String search, Long tallerId, BigDecimal minPrecioBase, BigDecimal maxPrecioBase, BigDecimal minDuracionEstimadaHoras, BigDecimal maxDuracionEstimadaHoras, String estado, Pageable pageable) {
        Specification<Servicio> spec = ServicioSpecification.filterServicios(search, tallerId, minPrecioBase, maxPrecioBase, minDuracionEstimadaHoras, maxDuracionEstimadaHoras, estado);
        return servicioRepository.findAll(spec, pageable).map(servicioMapper::toServicioResponse);
    }

    @Override
    public Page<ServicioResponse> findMyTallerServiciosByFilters(String search, BigDecimal minPrecioBase, BigDecimal maxPrecioBase, BigDecimal minDuracionEstimadaHoras, BigDecimal maxDuracionEstimadaHoras, String estado, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));

        Long tallerId = trabajador.getTaller().getId(); // Obtener el ID del taller del trabajador autenticado

        Specification<Servicio> spec = ServicioSpecification.filterServicios(search, tallerId, minPrecioBase, maxPrecioBase, minDuracionEstimadaHoras, maxDuracionEstimadaHoras, estado);
        return servicioRepository.findAll(spec, pageable).map(servicioMapper::toServicioResponse);
    }
}