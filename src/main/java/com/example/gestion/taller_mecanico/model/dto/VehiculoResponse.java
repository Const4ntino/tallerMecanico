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
public class VehiculoResponse {

    private Long id;
    private ClienteResponse cliente;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    private String motor;
    private String tipoVehiculo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
