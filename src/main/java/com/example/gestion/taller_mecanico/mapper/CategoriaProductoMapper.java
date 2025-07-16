package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.CategoriaProductoResponse;
import com.example.gestion.taller_mecanico.model.entity.CategoriaProducto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaProductoMapper {
    CategoriaProductoResponse toCategoriaProductoResponse(CategoriaProducto categoriaProducto);
    CategoriaProducto toCategoriaProducto(CategoriaProductoRequest categoriaProductoRequest);
}
