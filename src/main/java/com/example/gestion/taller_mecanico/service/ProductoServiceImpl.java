package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.CategoriaProductoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.ProductoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.mapper.ProductoMapper;
import com.example.gestion.taller_mecanico.model.dto.ProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.ProductoResponse;
import com.example.gestion.taller_mecanico.model.entity.CategoriaProducto;
import com.example.gestion.taller_mecanico.model.entity.Producto;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Trabajador; // Importar Trabajador
import com.example.gestion.taller_mecanico.repository.CategoriaProductoRepository;
import com.example.gestion.taller_mecanico.repository.ProductoRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final TallerRepository tallerRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final ProductoMapper productoMapper;
    private final TrabajadorRepository trabajadorRepository;

    @Override
    public List<ProductoResponse> findAll() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toProductoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponse findById(Long id) {
        return productoRepository.findById(id)
                .map(productoMapper::toProductoResponse)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Override
    public ProductoResponse save(ProductoRequest productoRequest) {
        Taller taller = tallerRepository.findById(productoRequest.getTallerId())
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + productoRequest.getTallerId()));

        CategoriaProducto categoria = null;
        if (productoRequest.getCategoriaId() != null) {
            categoria = categoriaProductoRepository.findById(productoRequest.getCategoriaId())
                    .orElseThrow(() -> new CategoriaProductoNotFoundException("Categoría de producto no encontrada con ID: " + productoRequest.getCategoriaId()));
        }

        Producto producto = Producto.builder()
                .taller(taller)
                .nombre(productoRequest.getNombre())
                .descripcion(productoRequest.getDescripcion())
                .categoria(categoria)
                .imageUrl(productoRequest.getImageUrl())
                .precio(productoRequest.getPrecio())
                .stock(productoRequest.getStock())
                .build();
        return productoMapper.toProductoResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse update(Long id, ProductoRequest productoRequest) {
        return productoRepository.findById(id)
                .map(producto -> {
                    Taller taller = tallerRepository.findById(productoRequest.getTallerId())
                            .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + productoRequest.getTallerId()));

                    CategoriaProducto categoria = null;
                    if (productoRequest.getCategoriaId() != null) {
                        categoria = categoriaProductoRepository.findById(productoRequest.getCategoriaId())
                                .orElseThrow(() -> new CategoriaProductoNotFoundException("Categoría de producto no encontrada con ID: " + productoRequest.getCategoriaId()));
                    }

                    producto.setTaller(taller);
                    producto.setNombre(productoRequest.getNombre());
                    producto.setDescripcion(productoRequest.getDescripcion());
                    producto.setCategoria(categoria);
                    producto.setImageUrl(productoRequest.getImageUrl());
                    producto.setPrecio(productoRequest.getPrecio());
                    producto.setStock(productoRequest.getStock());
                    return productoMapper.toProductoResponse(productoRepository.save(producto));
                }).orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ProductoNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    public Page<ProductoResponse> findProductosByFilters(String search, Long tallerId, Long categoriaId, BigDecimal minPrecio, BigDecimal maxPrecio, Integer minStock, Integer maxStock, Pageable pageable) {
        Specification<Producto> spec = ProductoSpecification.filterProductos(search, tallerId, categoriaId, minPrecio, maxPrecio, minStock, maxStock);
        return productoRepository.findAll(spec, pageable).map(productoMapper::toProductoResponse);
    }

    @Override
    public Page<ProductoResponse> findMyTallerProductosByFilters(String search, Long categoriaId, Integer minStock, Integer maxStock, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));

        Long tallerId = trabajador.getTaller().getId(); // Obtener el ID del taller del trabajador autenticado

        Specification<Producto> spec = ProductoSpecification.filterProductos(search, tallerId, categoriaId, null, null, minStock, maxStock);
        return productoRepository.findAll(spec, pageable).map(productoMapper::toProductoResponse);
    }
}
