package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.EmpresaRequest;
import com.example.gestion.taller_mecanico.model.dto.EmpresaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EmpresaService {
    List<EmpresaResponse> findAll();
    EmpresaResponse findById(Long id);
    EmpresaResponse findByRazon(String razon);
    EmpresaResponse findByRuc(String ruc);
    EmpresaResponse save(EmpresaRequest empresaRequest);
    EmpresaResponse update(Long id, EmpresaRequest empresaRequest);
    void deleteById(Long id);
    Page<EmpresaResponse> findEmpresasByFilters(String search, String ruc, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable);
}
