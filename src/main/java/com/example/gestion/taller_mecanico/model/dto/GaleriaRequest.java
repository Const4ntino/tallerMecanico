package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GaleriaRequest {

    @NotNull
    private Long tallerId;

    @NotBlank
    private String imagenUrl;

    private String titulo;

    private String descripcion;

    private String tipo;
}
