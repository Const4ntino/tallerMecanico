package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.CategoriaProductoNotFoundException;
import com.example.gestion.taller_mecanico.mapper.CategoriaProductoMapper;
import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoResponse;
import com.example.gestion.taller_mecanico.model.entity.CategoriaProducto;
import com.example.gestion.taller_mecanico.repository.CategoriaProductoRepository;
import com.example.gestion.taller_mecanico.specification.CategoriaProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaProductoServiceImpl implements CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final CategoriaProductoMapper categoriaProductoMapper;

    @Override
    public List<CategoriaProductoResponse> findAll() {
        return categoriaProductoRepository.findAll().stream()
                .map(categoriaProductoMapper::toCategoriaProductoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaProductoResponse findById(Long id) {
        return categoriaProductoRepository.findById(id)
                .map(categoriaProductoMapper::toCategoriaProductoResponse)
                .orElseThrow(() -> new CategoriaProductoNotFoundException("Categoría de producto no encontrada con ID: " + id));
    }

    @Override
    public CategoriaProductoResponse findByNombre(String nombre) {
        return categoriaProductoRepository.findByNombre(nombre)
                .map(categoriaProductoMapper::toCategoriaProductoResponse)
                .orElseThrow(() -> new CategoriaProductoNotFoundException("Categoría de producto no encontrada con nombre: " + nombre));
    }

    @Override
    public CategoriaProductoResponse save(CategoriaProductoRequest categoriaProductoRequest) {
        CategoriaProducto categoriaProducto = categoriaProductoMapper.toCategoriaProducto(categoriaProductoRequest);
        return categoriaProductoMapper.toCategoriaProductoResponse(categoriaProductoRepository.save(categoriaProducto));
    }

    @Override
    public CategoriaProductoResponse update(Long id, CategoriaProductoRequest categoriaProductoRequest) {
        return categoriaProductoRepository.findById(id)
                .map(categoriaProducto -> {
                    categoriaProducto.setNombre(categoriaProductoRequest.getNombre());
                    categoriaProducto.setDescripcion(categoriaProductoRequest.getDescripcion());
                    return categoriaProductoMapper.toCategoriaProductoResponse(categoriaProductoRepository.save(categoriaProducto));
                }).orElseThrow(() -> new CategoriaProductoNotFoundException("Categoría de producto no encontrada con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!categoriaProductoRepository.existsById(id)) {
            throw new CategoriaProductoNotFoundException("Categoría de producto no encontrada con ID: " + id);
        }
        categoriaProductoRepository.deleteById(id);
    }

    @Override
    public Page<CategoriaProductoResponse> findCategoriasProductoByFilters(String search, Pageable pageable) {
        Specification<CategoriaProducto> spec = CategoriaProductoSpecification.filterCategoriasProducto(search);
        return categoriaProductoRepository.findAll(spec, pageable).map(categoriaProductoMapper::toCategoriaProductoResponse);
    }
}