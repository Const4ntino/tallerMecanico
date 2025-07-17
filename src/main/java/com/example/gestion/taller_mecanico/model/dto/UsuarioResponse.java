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
public class UsuarioResponse {

    private Long id;
    private String nombreCompleto;
    private String dni;
    private String correo;
    private String username;
    private String rol;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
