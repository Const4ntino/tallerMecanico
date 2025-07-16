package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.ClienteResponse;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, TallerMapper.class})
public interface ClienteMapper {
    ClienteResponse toClienteResponse(Cliente cliente);
}
