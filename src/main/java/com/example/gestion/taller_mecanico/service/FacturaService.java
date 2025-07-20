package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.CalculatedTotalResponse;
import com.example.gestion.taller_mecanico.model.dto.FacturaRequest;
import com.example.gestion.taller_mecanico.model.dto.FacturaResponse;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import com.example.gestion.taller_mecanico.utils.enums.MetodoPago;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FacturaService {
    List<FacturaResponse> findAll();
    FacturaResponse findById(Long id);
    FacturaResponse save(FacturaRequest facturaRequest);
    FacturaResponse save(FacturaRequest facturaRequest, MultipartFile imagenOperacion);
    FacturaResponse update(Long id, FacturaRequest facturaRequest);
    FacturaResponse update(Long id, FacturaRequest facturaRequest, MultipartFile imagenOperacion);
    void deleteById(Long id);
    FacturaResponse findFacturaDetailsById(Long id);
    Page<FacturaResponse> findFacturasByFilters(String search, Long mantenimientoId, Long clienteId, Long tallerId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, MetodoPago metodoPago, Pageable pageable);
    Page<FacturaResponse> findMyFacturasByFilters(String search, Long mantenimientoId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, MetodoPago metodoPago, Pageable pageable);
    Page<FacturaResponse> findFacturasByTallerId(Long tallerId, String search, Long mantenimientoId, Long clienteId, LocalDateTime fechaEmisionDesde, LocalDateTime fechaEmisionHasta, BigDecimal minTotal, BigDecimal maxTotal, MetodoPago metodoPago, Pageable pageable);
    Page<MantenimientoResponse> findCompletedAndUnbilledMantenimientos(Pageable pageable);
    CalculatedTotalResponse calculateTotalForMantenimiento(Long mantenimientoId);
}
