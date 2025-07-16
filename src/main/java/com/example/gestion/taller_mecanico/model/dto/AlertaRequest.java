package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertaRequest {

    private Long vehiculoId;
    private Long clienteId;
    private Long tallerId;

    @NotBlank
    private String tipo;

    @NotBlank
    private String mensaje;

    @NotBlank
    private String estado;
}
