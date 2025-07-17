package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.AlertaEstadoRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaResponse;
import com.example.gestion.taller_mecanico.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<AlertaResponse>> findAll() {
        return ResponseEntity.ok(alertaService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @PostMapping
    public ResponseEntity<AlertaResponse> save(@Valid @RequestBody AlertaRequest alertaRequest) {
        return new ResponseEntity<>(alertaService.save(alertaRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @PostMapping("/manual")
    public ResponseEntity<AlertaResponse> saveManualAlerta(@Valid @RequestBody AlertaRequest alertaRequest) {
        return new ResponseEntity<>(alertaService.save(alertaRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponse> update(@PathVariable Long id, @Valid @RequestBody AlertaRequest alertaRequest) {
        return ResponseEntity.ok(alertaService.update(id, alertaRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        alertaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> updateAlertaEstado(@PathVariable Long id, @Valid @RequestBody AlertaEstadoRequest alertaEstadoRequest) {
        return alertaService.updateAlertaEstado(id, alertaEstadoRequest);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PatchMapping("/{id}/marcar-vista")
    public ResponseEntity<AlertaResponse> markAlertaAsViewed(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.markAlertaAsViewed(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<AlertaResponse>> findAlertas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long tallerId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            Pageable pageable) {
        Page<AlertaResponse> alertas = alertaService.findAlertasByFilters(
                search, vehiculoId, clienteId, tallerId, tipo, estado, fechaCreacionDesde, fechaCreacionHasta, pageable
        );
        return ResponseEntity.ok(alertas);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-alertas/filtrar")
    public ResponseEntity<Page<AlertaResponse>> findMyAlertas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            Pageable pageable) {
        Page<AlertaResponse> alertas = alertaService.findMyAlertasByFilters(
                search, vehiculoId, tipo, estado, fechaCreacionDesde, fechaCreacionHasta, pageable
        );
        return ResponseEntity.ok(alertas);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @GetMapping("/taller/{tallerId}/filtrar")
    public ResponseEntity<Page<AlertaResponse>> findAlertasByTaller(
            @PathVariable Long tallerId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            Pageable pageable) {
        Page<AlertaResponse> alertas = alertaService.findAlertasByTallerId(
                tallerId, search, vehiculoId, clienteId, tipo, estado, fechaCreacionDesde, fechaCreacionHasta, pageable
        );
        return ResponseEntity.ok(alertas);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/pendientes")
    public ResponseEntity<Page<AlertaResponse>> findPendingAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertaService.findPendingAlerts(pageable));
    }
}
