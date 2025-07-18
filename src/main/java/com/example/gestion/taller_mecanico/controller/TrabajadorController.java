package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.TrabajadorRequest;
import com.example.gestion.taller_mecanico.model.dto.TrabajadorResponse;
import com.example.gestion.taller_mecanico.service.TrabajadorService;
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
@RequestMapping("/api/trabajadores")
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping
    public ResponseEntity<List<TrabajadorResponse>> findAll() {
        return ResponseEntity.ok(trabajadorService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(trabajadorService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<TrabajadorResponse> findByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(trabajadorService.findByUsuarioId(usuarioId));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<TrabajadorResponse> save(@Valid @RequestBody TrabajadorRequest trabajadorRequest) {
        return new ResponseEntity<>(trabajadorService.save(trabajadorRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorResponse> update(@PathVariable Long id, @Valid @RequestBody TrabajadorRequest trabajadorRequest) {
        return ResponseEntity.ok(trabajadorService.update(id, trabajadorRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        trabajadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<TrabajadorResponse>> findTrabajadores(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) Long tallerId,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            Pageable pageable) {
        Page<TrabajadorResponse> trabajadores = trabajadorService.findTrabajadoresByFilters(
                search, especialidad, tallerId, rol, fechaCreacionDesde, fechaCreacionHasta, pageable
        );
        return ResponseEntity.ok(trabajadores);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR_TALLER')")
    @PutMapping("/mi-taller")
    public ResponseEntity<Long> obtenerMiTaller() {

        // Aquí asumo que el usuario está relacionado con un Trabajador que a su vez tiene un Taller
        Long tallerId = trabajadorService.obtenerTallerIdPorUsuarioId();
        return ResponseEntity.ok(tallerId);
    }
}