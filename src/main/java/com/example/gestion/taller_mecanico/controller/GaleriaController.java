package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.GaleriaRequest;
import com.example.gestion.taller_mecanico.model.dto.GaleriaResponse;
import com.example.gestion.taller_mecanico.service.GaleriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/galerias")
public class GaleriaController {

    private final GaleriaService galeriaService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<GaleriaResponse>> findAll() {
        return ResponseEntity.ok(galeriaService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<GaleriaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(galeriaService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<GaleriaResponse> save(@Valid @RequestBody GaleriaRequest galeriaRequest) {
        return new ResponseEntity<>(galeriaService.save(galeriaRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<GaleriaResponse> update(@PathVariable Long id, @Valid @RequestBody GaleriaRequest galeriaRequest) {
        return ResponseEntity.ok(galeriaService.update(id, galeriaRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        galeriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<GaleriaResponse>> findGalerias(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long tallerId,
            @RequestParam(required = false) String tipo,
            Pageable pageable) {
        Page<GaleriaResponse> galerias = galeriaService.findGaleriasByFilters(
                search, tallerId, tipo, pageable
        );
        return ResponseEntity.ok(galerias);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/mi-taller/filtrar")
    public ResponseEntity<Page<GaleriaResponse>> findMyTallerGalerias(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            Pageable pageable) {
        Page<GaleriaResponse> galerias = galeriaService.findMyTallerGaleriasByFilters(
                search, tipo, pageable
        );
        return ResponseEntity.ok(galerias);
    }
}
