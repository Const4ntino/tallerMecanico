package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.EmpresaRequest;
import com.example.gestion.taller_mecanico.model.dto.EmpresaResponse;
import com.example.gestion.taller_mecanico.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> getAllEmpresas() {
        return ResponseEntity.ok(empresaService.findAll());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> getEmpresaById(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.findById(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/razon/{razon}")
    public ResponseEntity<EmpresaResponse> getEmpresaByRazon(@PathVariable String razon) {
        return ResponseEntity.ok(empresaService.findByRazon(razon));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<EmpresaResponse> getEmpresaByRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(empresaService.findByRuc(ruc));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<EmpresaResponse> createEmpresa(@Valid @RequestBody EmpresaRequest empresaRequest) {
        return new ResponseEntity<>(empresaService.save(empresaRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> updateEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaRequest empresaRequest) {
        return ResponseEntity.ok(empresaService.update(id, empresaRequest));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long id) {
        empresaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/filter")
    public ResponseEntity<Page<EmpresaResponse>> getEmpresasByFilters(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String ruc,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacionHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(empresaService.findEmpresasByFilters(search, ruc, fechaCreacionDesde, fechaCreacionHasta, pageable));
    }
}
