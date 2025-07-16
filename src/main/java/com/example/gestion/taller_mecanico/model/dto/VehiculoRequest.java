package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoRequest {

    @NotNull
    private Long clienteId;

    @NotBlank
    @Size(max = 15)
    private String placa;

    @Size(max = 50)
    private String marca;

    @Size(max = 50)
    private String modelo;

    private Integer anio;

    @Size(max = 100)
    private String motor;

    @Size(max = 50)
    private String tipoVehiculo;

    @NotBlank
    private String estado;
}
