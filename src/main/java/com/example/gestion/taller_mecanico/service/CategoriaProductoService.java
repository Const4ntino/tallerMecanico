package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaProductoService {
    List<CategoriaProductoResponse> findAll();
    CategoriaProductoResponse findById(Long id);
    CategoriaProductoResponse findByNombre(String nombre);
    CategoriaProductoResponse save(CategoriaProductoRequest categoriaProductoRequest);
    CategoriaProductoResponse update(Long id, CategoriaProductoRequest categoriaProductoRequest);
    void deleteById(Long id);
    Page<CategoriaProductoResponse> findCategoriasProductoByFilters(String search, Pageable pageable);
}