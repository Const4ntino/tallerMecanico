package com.example.gestion.taller_mecanico.model.dto;

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
public class FacturaRequest {

    @NotNull
    private Long mantenimientoId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long tallerId;

    private String detalles;

    private String pdfUrl;

    @NotNull
    private String metodoPago;

    private String nroOperacion;

    private String imagenOperacion;
}

