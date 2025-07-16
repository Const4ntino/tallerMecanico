package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoResponse;
import com.example.gestion.taller_mecanico.service.CategoriaProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias-producto")
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaProductoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<CategoriaProductoResponse>> findAll() {
        return ResponseEntity.ok(categoriaProductoService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaProductoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<CategoriaProductoResponse> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(categoriaProductoService.findByNombre(nombre));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<CategoriaProductoResponse> save(@Valid @RequestBody CategoriaProductoRequest categoriaProductoRequest) {
        return new ResponseEntity<>(categoriaProductoService.save(categoriaProductoRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponse> update(@PathVariable Long id, @Valid @RequestBody CategoriaProductoRequest categoriaProductoRequest) {
        return ResponseEntity.ok(categoriaProductoService.update(id, categoriaProductoRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        categoriaProductoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<CategoriaProductoResponse>> findCategoriasProducto(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<CategoriaProductoResponse> categorias = categoriaProductoService.findCategoriasProductoByFilters(
                search, pageable
        );
        return ResponseEntity.ok(categorias);
    }
}