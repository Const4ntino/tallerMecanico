package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.UsuarioResponse;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponse toUsuarioResponse(Usuario usuario);
}
