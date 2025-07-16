package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.ProductoResponse;
import com.example.gestion.taller_mecanico.model.entity.Producto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TallerMapper.class, CategoriaProductoMapper.class})
public interface ProductoMapper {
    ProductoResponse toProductoResponse(Producto producto);
}
