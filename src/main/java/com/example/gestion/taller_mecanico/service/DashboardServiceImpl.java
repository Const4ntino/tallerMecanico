package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.DashboardSummaryResponse;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.FacturaRepository;
import com.example.gestion.taller_mecanico.repository.MantenimientoRepository;
import com.example.gestion.taller_mecanico.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MantenimientoRepository mantenimientoRepository;
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    public DashboardSummaryResponse getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate, String groupBy) {
        // Total de mantenimientos (con filtro de fechas si se proporcionan)
        long totalMantenimientos = (startDate != null && endDate != null) ?
                mantenimientoRepository.countByFechaCreacionBetween(startDate, endDate) :
                mantenimientoRepository.count();

        // Total de clientes
        long totalClientes = clienteRepository.count();

        // Total de vehículos
        long totalVehiculos = vehiculoRepository.count();

        // Total de ingresos y ingresos por período
        BigDecimal totalIngresos = BigDecimal.ZERO;
        Map<String, BigDecimal> ingresosPorPeriodo = new LinkedHashMap<>();

        List<Object[]> ingresosData = (startDate != null && endDate != null) ?
                facturaRepository.findTotalIngresosAndGroupByDate(startDate, endDate) :
                facturaRepository.findTotalIngresosAndGroupByDate(); // Obtener todos los ingresos si no hay filtro de fecha

        if (groupBy != null) {
            if (groupBy.equalsIgnoreCase("MONTH")) {
                ingresosPorPeriodo = ingresosData.stream()
                        .collect(Collectors.groupingBy(
                                row -> ((LocalDateTime) row[1]).format(DateTimeFormatter.ofPattern("yyyy-MM")),
                                Collectors.reducing(BigDecimal.ZERO, row -> (BigDecimal) row[0], BigDecimal::add)
                        ));
            } else if (groupBy.equalsIgnoreCase("YEAR")) {
                ingresosPorPeriodo = ingresosData.stream()
                        .collect(Collectors.groupingBy(
                                row -> ((LocalDateTime) row[1]).format(DateTimeFormatter.ofPattern("yyyy")),
                                Collectors.reducing(BigDecimal.ZERO, row -> (BigDecimal) row[0], BigDecimal::add)
                        ));
            }

            // Sumar el total de ingresos siempre, aunque haya groupBy
            totalIngresos = ingresosData.stream()
                    .map(row -> (BigDecimal) row[0])
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // Si no hay groupBy, sumar todos los ingresos para el total general
            totalIngresos = ingresosData.stream()
                    .map(row -> (BigDecimal) row[0])
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }


        return DashboardSummaryResponse.builder()
                .totalMantenimientos(totalMantenimientos)
                .totalClientes(totalClientes)
                .totalVehiculos(totalVehiculos)
                .totalIngresos(totalIngresos)
                .ingresosPorPeriodo(ingresosPorPeriodo)
                .build();
    }
}
