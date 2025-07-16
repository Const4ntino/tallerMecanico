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
public class FacturaResponse {

    private Long id;
    private MantenimientoResponse mantenimiento;
    private ClienteResponse cliente;
    private TallerResponse taller;
    private LocalDateTime fechaEmision;
    private BigDecimal total;
    private String detalles;
    private String pdfUrl;
}
