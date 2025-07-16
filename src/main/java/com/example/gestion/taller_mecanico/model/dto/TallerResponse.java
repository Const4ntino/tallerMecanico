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
public class TallerResponse {

    private Long id;
    private String nombre;
    private String ciudad;
    private String direccion;
    private String logoUrl;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
