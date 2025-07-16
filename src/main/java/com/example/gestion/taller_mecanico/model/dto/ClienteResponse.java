package com.example.gestion.taller_mecanico.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponse {

    private Long id;
    private UsuarioResponse usuario;
    private String telefono;
    private String direccion;
    private TallerResponse tallerAsignado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
