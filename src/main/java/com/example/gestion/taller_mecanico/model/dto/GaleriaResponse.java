package com.example.gestion.taller_mecanico.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GaleriaResponse {

    private Long id;
    private TallerResponse taller;
    private String imagenUrl;
    private String titulo;
    private String descripcion;
    private String tipo;
}
