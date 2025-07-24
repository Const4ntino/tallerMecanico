package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.CalculatedTotalResponse;
import com.example.gestion.taller_mecanico.model.dto.FacturaRequest;
import com.example.gestion.taller_mecanico.model.dto.FacturaResponse;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import com.example.gestion.taller_mecanico.service.FacturaService;
import com.example.gestion.taller_mecanico.utils.enums.MetodoPago;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<FacturaResponse>> findAll() {
        return ResponseEntity.ok(facturaService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping
    public ResponseEntity<FacturaResponse> save(@Valid @RequestBody FacturaRequest facturaRequest) {
        return new ResponseEntity<>(facturaService.save(facturaRequest), HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping(value = "/con-imagen", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<FacturaResponse> saveWithImage(
            @RequestPart("factura") @Valid FacturaRequest facturaRequest,
            @RequestPart(value = "imagenOperacion", required = false) MultipartFile imagenOperacion,
            @RequestParam(value = "conIgv", defaultValue = "false") boolean conIgv,
            @RequestParam(value = "ruc", required = false) String ruc) {
        return new ResponseEntity<>(facturaService.save(facturaRequest, imagenOperacion, conIgv, ruc), HttpStatus.CREATED);
    }   

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponse> update(@PathVariable Long id, @Valid @RequestBody FacturaRequest facturaRequest) {
        return ResponseEntity.ok(facturaService.update(id, facturaRequest));
    }
    
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PutMapping(value = "/{id}/con-imagen", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<FacturaResponse> updateWithImage(
            @PathVariable Long id,
            @RequestPart("factura") @Valid FacturaRequest facturaRequest,
            @RequestPart(value = "imagenOperacion", required = false) MultipartFile imagenOperacion,
            @RequestParam(value = "conIgv", defaultValue = "false") boolean conIgv,
            @RequestParam(value = "ruc", required = false) String ruc) {
        return ResponseEntity.ok(facturaService.update(id, facturaRequest, imagenOperacion, conIgv, ruc));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        facturaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR', 'CLIENTE')")
    @GetMapping("/{id}/details")
    public ResponseEntity<FacturaResponse> findFacturaDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.findFacturaDetailsById(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<FacturaResponse>> findFacturas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long mantenimientoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long tallerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEmisionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEmisionHasta,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @RequestParam(required = false) MetodoPago metodoPago,
            Pageable pageable) {
        Page<FacturaResponse> facturas = facturaService.findFacturasByFilters(
                search, mantenimientoId, clienteId, tallerId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal, metodoPago, pageable
        );
        return ResponseEntity.ok(facturas);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-facturas/filtrar")
    public ResponseEntity<Page<FacturaResponse>> findMyFacturas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long mantenimientoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEmisionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEmisionHasta,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @RequestParam(required = false) MetodoPago metodoPago,
            Pageable pageable) {
        Page<FacturaResponse> facturas = facturaService.findMyFacturasByFilters(
                search, mantenimientoId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal, metodoPago, pageable
        );
        return ResponseEntity.ok(facturas);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR_TALLER')")
    @GetMapping("/taller/{tallerId}/filtrar")
    public ResponseEntity<Page<FacturaResponse>> findFacturasByTaller(
            @PathVariable Long tallerId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long mantenimientoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEmisionDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEmisionHasta,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @RequestParam(required = false) MetodoPago metodoPago,
            Pageable pageable) {
        Page<FacturaResponse> facturas = facturaService.findFacturasByTallerId(
                tallerId, search, mantenimientoId, clienteId, fechaEmisionDesde, fechaEmisionHasta, minTotal, maxTotal, metodoPago, pageable
        );
        return ResponseEntity.ok(facturas);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/mantenimientos-pendientes-facturar")
    public ResponseEntity<Page<MantenimientoResponse>> findCompletedAndUnbilledMantenimientos(Pageable pageable) {
        return ResponseEntity.ok(facturaService.findCompletedAndUnbilledMantenimientos(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/calcular-total/{mantenimientoId}")
    public ResponseEntity<CalculatedTotalResponse> calculateTotalForMantenimiento(@PathVariable Long mantenimientoId) {
        return ResponseEntity.ok(facturaService.calculateTotalForMantenimiento(mantenimientoId));
    }
}
