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
public class AlertaResponse {

    private Long id;
    private VehiculoResponse vehiculo;
    private ClienteResponse cliente;
    private TallerResponse taller;
    private String tipo;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaCreacion;
}
