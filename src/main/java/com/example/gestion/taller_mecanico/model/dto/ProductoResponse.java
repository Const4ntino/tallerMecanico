package com.example.gestion.taller_mecanico.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponse {

    private Long id;
    private TallerResponse taller;
    private String nombre;
    private String descripcion;
    private CategoriaProductoResponse categoria;
    private String imageUrl;
    private BigDecimal precio;
    private Integer stock;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
