package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.TrabajadorResponse;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, TallerMapper.class})
public interface TrabajadorMapper {
    TrabajadorResponse toTrabajadorResponse(Trabajador trabajador);
}
