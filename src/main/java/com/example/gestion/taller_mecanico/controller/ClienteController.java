package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.ClienteRequest;
import com.example.gestion.taller_mecanico.model.dto.ClienteResponse;
import com.example.gestion.taller_mecanico.model.dto.VehiculoResponse;
import com.example.gestion.taller_mecanico.service.ClienteService;
import com.example.gestion.taller_mecanico.service.VehiculoService;
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
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final VehiculoService vehiculoService; // Inyectar VehiculoService

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ClienteResponse> findByUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(clienteService.findByUsuarioId(usuarioId));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<ClienteResponse> save(@Valid @RequestBody ClienteRequest clienteRequest) {
        return new ResponseEntity<>(clienteService.save(clienteRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable Long id, @Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.ok(clienteService.update(id, clienteRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<ClienteResponse>> findClientes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long tallerAsignadoId,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            Pageable pageable) {
        Page<ClienteResponse> clientes = clienteService.findClientesByFilters(
                search, tallerAsignadoId, telefono, fechaCreacionDesde, fechaCreacionHasta, pageable
        );
        return ResponseEntity.ok(clientes);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-vehiculos/filtrar")
    public ResponseEntity<Page<VehiculoResponse>> findMyVehiculos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            Pageable pageable) {
        Page<VehiculoResponse> vehiculos = vehiculoService.findMyVehiculosByFilters(
                search, estado, pageable
        );
        return ResponseEntity.ok(vehiculos);
    }
}
