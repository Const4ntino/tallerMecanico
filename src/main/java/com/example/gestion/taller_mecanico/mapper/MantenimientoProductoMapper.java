package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.MantenimientoProductoRequest;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoProductoResponse;
import com.example.gestion.taller_mecanico.model.entity.MantenimientoProducto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductoMapper.class})
public interface MantenimientoProductoMapper {

    @Mapping(source = "mantenimiento.id", target = "mantenimientoId")
    @Mapping(source = "producto", target = "producto")
    MantenimientoProductoResponse toMantenimientoProductoResponse(MantenimientoProducto mantenimientoProducto);

    @Mapping(source = "productoId", target = "producto.id")
    @Mapping(target = "id.productoId", source = "productoId")
    @Mapping(target = "id.mantenimientoId", ignore = true) // Se establecerá en el servicio
    @Mapping(target = "mantenimiento", ignore = true) // Se establecerá en el servicio
    MantenimientoProducto toMantenimientoProducto(MantenimientoProductoRequest request);
}
