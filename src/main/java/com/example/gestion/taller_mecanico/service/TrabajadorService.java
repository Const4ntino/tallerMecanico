package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.TrabajadorRequest;
import com.example.gestion.taller_mecanico.model.dto.TrabajadorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TrabajadorService {

    List<TrabajadorResponse> findAll();
    TrabajadorResponse findById(Long id);
    TrabajadorResponse findByUsuarioId(Long usuarioId);
    TrabajadorResponse save(TrabajadorRequest trabajadorRequest);
    TrabajadorResponse update(Long id, TrabajadorRequest trabajadorRequest);
    void deleteById(Long id);
    Page<TrabajadorResponse> findTrabajadoresByFilters(String search, String especialidad, Long tallerId, String rol, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable);
}