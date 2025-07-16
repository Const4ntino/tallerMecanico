package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequest {

    @NotNull
    private Long tallerId;

    @NotBlank
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    private Long categoriaId;

    private String imageUrl;

    @NotNull
    @Min(0)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    private Integer stock;
}
