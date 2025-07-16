package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.TallerRequest;
import com.example.gestion.taller_mecanico.model.dto.TallerResponse;
import com.example.gestion.taller_mecanico.service.TallerService;
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
@RequestMapping("/api/talleres")
public class TallerController {

    private final TallerService tallerService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<TallerResponse>> findAll() {
        return ResponseEntity.ok(tallerService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<TallerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<TallerResponse> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(tallerService.findByNombre(nombre));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<TallerResponse> save(@Valid @RequestBody TallerRequest tallerRequest) {
        return new ResponseEntity<>(tallerService.save(tallerRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<TallerResponse> update(@PathVariable Long id, @Valid @RequestBody TallerRequest tallerRequest) {
        return ResponseEntity.ok(tallerService.update(id, tallerRequest));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        tallerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<TallerResponse>> findTalleres(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaActualizacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaActualizacionHasta,
            Pageable pageable) {
        Page<TallerResponse> talleres = tallerService.findTalleresByFilters(
                search, ciudad, estado, fechaCreacionDesde, fechaCreacionHasta, fechaActualizacionDesde, fechaActualizacionHasta, pageable
        );
        return ResponseEntity.ok(talleres);
    }
}