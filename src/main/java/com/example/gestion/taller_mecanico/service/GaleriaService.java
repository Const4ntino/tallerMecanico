package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.GaleriaRequest;
import com.example.gestion.taller_mecanico.model.dto.GaleriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GaleriaService {
    List<GaleriaResponse> findAll();
    GaleriaResponse findById(Long id);
    GaleriaResponse save(GaleriaRequest galeriaRequest);
    GaleriaResponse update(Long id, GaleriaRequest galeriaRequest);
    void deleteById(Long id);
    Page<GaleriaResponse> findGaleriasByFilters(String search, Long tallerId, String tipo, Pageable pageable);
    Page<GaleriaResponse> findMyTallerGaleriasByFilters(String search, String tipo, Pageable pageable);
}
