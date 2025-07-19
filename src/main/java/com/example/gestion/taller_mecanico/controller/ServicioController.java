package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.ServicioRequest;
import com.example.gestion.taller_mecanico.model.dto.ServicioResponse;
import com.example.gestion.taller_mecanico.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<ServicioResponse>> findAll() {
        return ResponseEntity.ok(servicioService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<ServicioResponse> save(@Valid @RequestBody ServicioRequest servicioRequest) {
        return new ResponseEntity<>(servicioService.save(servicioRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponse> update(@PathVariable Long id, @Valid @RequestBody ServicioRequest servicioRequest) {
        return ResponseEntity.ok(servicioService.update(id, servicioRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        servicioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'CLIENTE')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<ServicioResponse>> findServicios(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long tallerId,
            @RequestParam(required = false) BigDecimal minPrecioBase,
            @RequestParam(required = false) BigDecimal maxPrecioBase,
            @RequestParam(required = false) BigDecimal minDuracionEstimadaHoras,
            @RequestParam(required = false) BigDecimal maxDuracionEstimadaHoras,
            @RequestParam(required = false) String estado,
            Pageable pageable) {
        Page<ServicioResponse> servicios = servicioService.findServiciosByFilters(
                search, tallerId, minPrecioBase, maxPrecioBase, minDuracionEstimadaHoras, maxDuracionEstimadaHoras, estado, pageable
        );
        return ResponseEntity.ok(servicios);
    }

    @PreAuthorize("hasRole('TRABAJADOR')")
    @GetMapping("/mi-taller/filtrar")
    public ResponseEntity<Page<ServicioResponse>> findMyTallerServicios(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrecioBase,
            @RequestParam(required = false) BigDecimal maxPrecioBase,
            @RequestParam(required = false) BigDecimal minDuracionEstimadaHoras,
            @RequestParam(required = false) BigDecimal maxDuracionEstimadaHoras,
            @RequestParam(required = false) String estado,
            Pageable pageable) {
        Page<ServicioResponse> servicios = servicioService.findMyTallerServiciosByFilters(
                search, minPrecioBase, maxPrecioBase, minDuracionEstimadaHoras, maxDuracionEstimadaHoras, estado, pageable
        );
        return ResponseEntity.ok(servicios);
    }
}