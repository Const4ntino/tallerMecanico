package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoRequest {

    @NotNull
    private Long vehiculoId;

    @NotNull
    private Long servicioId;

    private Long trabajadorId;

    @NotBlank
    private String estado;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private String observacionesCliente;

    private String observacionesTrabajador;

    private List<MantenimientoProductoRequest> productosUsados;
}
