package com.example.gestion.taller_mecanico.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {
    private Long totalMantenimientos;
    private Long totalClientes;
    private Long totalVehiculos;
    private BigDecimal totalIngresos;
    private Map<String, BigDecimal> ingresosPorPeriodo; // Clave: "YYYY-MM" o "YYYY", Valor: Ingreso
}
