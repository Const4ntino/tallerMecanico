package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.ProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.ProductoResponse;
import com.example.gestion.taller_mecanico.service.ProductoService;
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
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<ProductoResponse> save(@Valid @RequestBody ProductoRequest productoRequest) {
        return new ResponseEntity<>(productoService.save(productoRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(@PathVariable Long id, @Valid @RequestBody ProductoRequest productoRequest) {
        return ResponseEntity.ok(productoService.update(id, productoRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<ProductoResponse>> findProductos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long tallerId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal minPrecio,
            @RequestParam(required = false) BigDecimal maxPrecio,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            Pageable pageable) {
        Page<ProductoResponse> productos = productoService.findProductosByFilters(
                search, tallerId, categoriaId, minPrecio, maxPrecio, minStock, maxStock, pageable
        );
        return ResponseEntity.ok(productos);
    }

    @PreAuthorize("hasRole('TRABAJADOR')")
    @GetMapping("/mi-taller/filtrar")
    public ResponseEntity<Page<ProductoResponse>> findMyTallerProductos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            Pageable pageable) {
        Page<ProductoResponse> productos = productoService.findMyTallerProductosByFilters(
                search, categoriaId, minStock, maxStock, pageable
        );
        return ResponseEntity.ok(productos);
    }
}