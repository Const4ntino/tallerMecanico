package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.MantenimientoRequest;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MantenimientoService {
    List<MantenimientoResponse> findAll();
    MantenimientoResponse findById(Long id);
    MantenimientoResponse save(MantenimientoRequest mantenimientoRequest);
    MantenimientoResponse update(Long id, MantenimientoRequest mantenimientoRequest);
    void deleteById(Long id);
    Page<MantenimientoResponse> findMantenimientosByFilters(String search, Long vehiculoId, Long servicioId, Long trabajadorId, String estado, LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde, LocalDateTime fechaFinHasta, Boolean estaFacturado, Pageable pageable);
    Page<MantenimientoResponse> findMyMantenimientosByFilters(String search, Long vehiculoId, String estado, LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde, LocalDateTime fechaFinHasta, Pageable pageable);
    Page<MantenimientoResponse> findMantenimientosByTallerId(Long tallerId, String search, Long vehiculoId, Long servicioId, Long trabajadorId, String estado, LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde, LocalDateTime fechaFinHasta, Pageable pageable);
    Page<MantenimientoResponse> findAssignedMantenimientos(String search, Long vehiculoId, String estado, LocalDateTime fechaInicioDesde, LocalDateTime fechaInicioHasta, LocalDateTime fechaFinDesde, LocalDateTime fechaFinHasta, Pageable pageable);
}
