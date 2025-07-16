package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.GaleriaRequest;
import com.example.gestion.taller_mecanico.model.dto.GaleriaResponse;
import com.example.gestion.taller_mecanico.model.entity.Galeria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TallerMapper.class})
public interface GaleriaMapper {

    GaleriaResponse toGaleriaResponse(Galeria galeria);

    @Mapping(source = "tallerId", target = "taller.id")
    Galeria toGaleria(GaleriaRequest galeriaRequest);
}
