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
public class TrabajadorResponse {

    private Long id;
    private UsuarioResponse usuario;
    private String especialidad;
    private TallerResponse taller;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
