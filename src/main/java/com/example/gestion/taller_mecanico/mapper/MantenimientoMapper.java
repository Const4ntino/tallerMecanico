package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.MantenimientoRequest;
import com.example.gestion.taller_mecanico.model.dto.MantenimientoResponse;
import com.example.gestion.taller_mecanico.model.entity.Mantenimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class, ServicioMapper.class, TrabajadorMapper.class, MantenimientoProductoMapper.class})
public interface MantenimientoMapper {
    @Mapping(source = "estado", target = "estado")
    @Mapping(target = "productosUsados", ignore = true) // Se mapeará manualmente en el servicio
    MantenimientoResponse toMantenimientoResponse(Mantenimiento mantenimiento);

    @Mapping(source = "vehiculoId", target = "vehiculo.id")
    @Mapping(source = "servicioId", target = "servicio.id")
    @Mapping(source = "trabajadorId", target = "trabajador.id")
    @Mapping(source = "estado", target = "estado")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Mantenimiento toMantenimiento(MantenimientoRequest mantenimientoRequest);
}
