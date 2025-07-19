package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.VehiculoClientRequest;
import com.example.gestion.taller_mecanico.model.dto.VehiculoRequest;
import com.example.gestion.taller_mecanico.model.dto.VehiculoResponse;
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
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> findAll() {
        return ResponseEntity.ok(vehiculoService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/placa/{placa}")
    public ResponseEntity<VehiculoResponse> findByPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(vehiculoService.findByPlaca(placa));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<VehiculoResponse> save(@Valid @RequestBody VehiculoRequest vehiculoRequest) {
        return new ResponseEntity<>(vehiculoService.save(vehiculoRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/cliente")
    public ResponseEntity<VehiculoResponse> saveClientVehiculo(@Valid @RequestBody VehiculoClientRequest vehiculoClientRequest) {
        return new ResponseEntity<>(vehiculoService.saveClientVehiculo(vehiculoClientRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponse> update(@PathVariable Long id, @Valid @RequestBody VehiculoRequest vehiculoRequest) {
        return ResponseEntity.ok(vehiculoService.update(id, vehiculoRequest));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/cliente/{id}")
    public ResponseEntity<VehiculoResponse> updateClientVehiculo(@PathVariable Long id, @Valid @RequestBody VehiculoClientRequest vehiculoClientRequest) {
        return ResponseEntity.ok(vehiculoService.updateClientVehiculo(id, vehiculoClientRequest));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        vehiculoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<VehiculoResponse>> findVehiculos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long tallerAsignadoId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            @RequestParam(required = false) Boolean excluirVehiculosEnMantenimiento,
            Pageable pageable) {
        Page<VehiculoResponse> vehiculos = vehiculoService.findVehiculosByFilters(
                search, clienteId, tallerAsignadoId, estado, fechaCreacionDesde, fechaCreacionHasta, excluirVehiculosEnMantenimiento, pageable
        );
        return ResponseEntity.ok(vehiculos);
    }

}