package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.ServicioRequest;
import com.example.gestion.taller_mecanico.model.dto.ServicioResponse;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicioService {
    List<ServicioResponse> findAll();
    ServicioResponse findById(Long id);
    ServicioResponse save(ServicioRequest servicioRequest);
    ServicioResponse update(Long id, ServicioRequest servicioRequest);
    void deleteById(Long id);
    Page<ServicioResponse> findServiciosByFilters(String search, Long tallerId, BigDecimal minPrecioBase, BigDecimal maxPrecioBase, BigDecimal minDuracionEstimadaHoras, BigDecimal maxDuracionEstimadaHoras, Pageable pageable);
    Page<ServicioResponse> findMyTallerServiciosByFilters(String search, BigDecimal minPrecioBase, BigDecimal maxPrecioBase, BigDecimal minDuracionEstimadaHoras, BigDecimal maxDuracionEstimadaHoras, Pageable pageable);
}
