package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.UsuarioClienteRequest;
import com.example.gestion.taller_mecanico.model.dto.UsuarioRequest;
import com.example.gestion.taller_mecanico.model.dto.UsuarioResponse;
import com.example.gestion.taller_mecanico.model.dto.UsuarioTrabajadorRequest;
import com.example.gestion.taller_mecanico.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<UsuarioResponse> save(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return new ResponseEntity<>(usuarioService.save(usuarioRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping("/cliente")
    public ResponseEntity<UsuarioResponse> createUsuarioCliente(@Valid @RequestBody UsuarioClienteRequest request) {
        return new ResponseEntity<>(usuarioService.createUsuarioCliente(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping("/trabajador")
    public ResponseEntity<UsuarioResponse> createUsuarioTrabajador(@Valid @RequestBody UsuarioTrabajadorRequest request) {
        return new ResponseEntity<>(usuarioService.createUsuarioTrabajador(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.ok(usuarioService.update(id, usuarioRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<UsuarioResponse>> findUsuarios(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaActualizacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaActualizacionHasta,
            Pageable pageable) {
        Page<UsuarioResponse> usuarios = usuarioService.findUsuariosByFilters(
                search, rol, fechaCreacionDesde, fechaCreacionHasta, fechaActualizacionDesde, fechaActualizacionHasta, pageable
        );
        return ResponseEntity.ok(usuarios);
    }
    
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/trabajadores-no-asignados")
    public ResponseEntity<List<UsuarioResponse>> findTrabajadoresNoAsignados() {
        return ResponseEntity.ok(usuarioService.findTrabajadoresNoAsignados());
    }
    
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/clientes-no-asignados")
    public ResponseEntity<List<UsuarioResponse>> findClientesNoAsignados() {
        return ResponseEntity.ok(usuarioService.findClientesNoAsignados());
    }
}