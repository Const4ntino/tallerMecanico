package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.ProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.ProductoResponse;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    List<ProductoResponse> findAll();
    ProductoResponse findById(Long id);
    ProductoResponse save(ProductoRequest productoRequest);
    ProductoResponse update(Long id, ProductoRequest productoRequest);
    void deleteById(Long id);
    Page<ProductoResponse> findProductosByFilters(String search, Long tallerId, Long categoriaId, BigDecimal minPrecio, BigDecimal maxPrecio, Integer minStock, Integer maxStock, Pageable pageable);
    Page<ProductoResponse> findMyTallerProductosByFilters(String search, Long categoriaId, Integer minStock, Integer maxStock, Pageable pageable);
}
