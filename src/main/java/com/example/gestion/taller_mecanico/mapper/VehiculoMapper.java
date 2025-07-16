package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.VehiculoResponse;
import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClienteMapper.class})
public interface VehiculoMapper {
    @Mapping(source = "estado", target = "estado")
    VehiculoResponse toVehiculoResponse(Vehiculo vehiculo);
}
