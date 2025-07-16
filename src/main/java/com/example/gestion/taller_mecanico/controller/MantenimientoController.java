package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.MantenimientoRequest;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import com.example.gestion.taller_mecanico.service.MantenimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping
    public ResponseEntity<List<MantenimientoResponse>> findAll() {
        return ResponseEntity.ok(mantenimientoService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @PostMapping
    public ResponseEntity<MantenimientoResponse> save(@Valid @RequestBody MantenimientoRequest mantenimientoRequest) {
        return new ResponseEntity<>(mantenimientoService.save(mantenimientoRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoResponse> update(@PathVariable Long id, @Valid @RequestBody MantenimientoRequest mantenimientoRequest) {
        return ResponseEntity.ok(mantenimientoService.update(id, mantenimientoRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        mantenimientoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Filtros para ADMINISTRADOR y ADMINISTRADOR_TALLER (todos los mantenimientos)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<MantenimientoResponse>> findMantenimientos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) Long servicioId,
            @RequestParam(required = false) Long trabajadorId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinHasta,
            Pageable pageable) {
        Page<MantenimientoResponse> mantenimientos = mantenimientoService.findMantenimientosByFilters(
                search, vehiculoId, servicioId, trabajadorId, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta, pageable
        );
        return ResponseEntity.ok(mantenimientos);
    }

    // Filtros para CLIENTE (solo sus mantenimientos)
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-mantenimientos/filtrar")
    public ResponseEntity<Page<MantenimientoResponse>> findMyMantenimientos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinHasta,
            Pageable pageable) {
        Page<MantenimientoResponse> mantenimientos = mantenimientoService.findMyMantenimientosByFilters(
                search, vehiculoId, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta, pageable
        );
        return ResponseEntity.ok(mantenimientos);
    }

    // Filtros para TRABAJADOR (solo sus mantenimientos asignados)
    @PreAuthorize("hasRole('TRABAJADOR')")
    @GetMapping("/asignados/filtrar")
    public ResponseEntity<Page<MantenimientoResponse>> findAssignedMantenimientos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinHasta,
            Pageable pageable) {
        Page<MantenimientoResponse> mantenimientos = mantenimientoService.findAssignedMantenimientos(
                search, vehiculoId, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta, pageable
        );
        return ResponseEntity.ok(mantenimientos);
    }

    // Filtros para ADMINISTRADOR_TALLER (mantenimientos de su taller)
    @PreAuthorize("hasRole('ADMINISTRADOR_TALLER')")
    @GetMapping("/taller/{tallerId}/filtrar")
    public ResponseEntity<Page<MantenimientoResponse>> findMantenimientosByTaller(
            @PathVariable Long tallerId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) Long servicioId,
            @RequestParam(required = false) Long trabajadorId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicioHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinHasta,
            Pageable pageable) {
        Page<MantenimientoResponse> mantenimientos = mantenimientoService.findMantenimientosByTallerId(
                tallerId, search, vehiculoId, servicioId, trabajadorId, estado, fechaInicioDesde, fechaInicioHasta, fechaFinDesde, fechaFinHasta, pageable
        );
        return ResponseEntity.ok(mantenimientos);
    }
}
