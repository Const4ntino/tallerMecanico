package com.example.gestion.taller_mecanico.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MantenimientoProductoResponse {

    private Long mantenimientoId;
    private ProductoResponse producto;
    private Integer cantidadUsada;
    private BigDecimal precioEnUso;
}
