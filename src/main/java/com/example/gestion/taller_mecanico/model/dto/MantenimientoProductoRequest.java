package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoProductoRequest {

    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidadUsada;

    @NotNull
    @Min(0)
    private BigDecimal precioEnUso;
}
