package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.TallerRequest;
import com.example.gestion.taller_mecanico.model.dto.TallerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TallerService {
    List<TallerResponse> findAll();
    TallerResponse findById(Long id);
    TallerResponse findByNombre(String nombre);
    TallerResponse save(TallerRequest tallerRequest);
    TallerResponse update(Long id, TallerRequest tallerRequest);
    void deleteById(Long id);
    Page<TallerResponse> findTalleresByFilters(String search, String ciudad, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, LocalDateTime fechaActualizacionDesde, LocalDateTime fechaActualizacionHasta, Pageable pageable);
}