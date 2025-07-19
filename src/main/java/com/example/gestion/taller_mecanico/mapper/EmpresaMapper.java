package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.EmpresaResponse;
import com.example.gestion.taller_mecanico.model.entity.Empresa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {
    EmpresaResponse toEmpresaResponse(Empresa empresa);
}
