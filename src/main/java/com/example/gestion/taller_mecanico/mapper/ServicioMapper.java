package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.ServicioResponse;
import com.example.gestion.taller_mecanico.model.entity.Servicio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TallerMapper.class})
public interface ServicioMapper {
    ServicioResponse toServicioResponse(Servicio servicio);
}
