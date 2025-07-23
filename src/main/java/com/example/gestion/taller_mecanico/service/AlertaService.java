package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.AlertaEstadoRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaService {
    List<AlertaResponse> findAll();
    AlertaResponse findById(Long id);
    AlertaResponse save(AlertaRequest alertaRequest);
    AlertaResponse update(Long id, AlertaRequest alertaRequest);
    ResponseEntity<?> updateAlertaEstado(Long id, AlertaEstadoRequest alertaEstadoRequest);
    AlertaResponse markAlertaAsViewed(Long id);
    Page<AlertaResponse> findPendingAlerts(Pageable pageable);
    void deleteById(Long id);
    Page<AlertaResponse> findAlertasByFilters(String search, Long vehiculoId, Long clienteId, Long tallerId, String tipo, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable);
    Page<AlertaResponse> findMyAlertasByFilters(String search, Long vehiculoId, String tipo, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable);
    Page<AlertaResponse> findAlertasByTallerId(Long tallerId, String search, Long vehiculoId, Long clienteId, String tipo, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable);
    Long countMyNewAlertas();
}
