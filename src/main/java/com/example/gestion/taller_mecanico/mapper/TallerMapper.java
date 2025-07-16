package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.TallerResponse;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TallerMapper {
    @Mapping(source = "estado", target = "estado")
    TallerResponse toTallerResponse(Taller taller);
}
