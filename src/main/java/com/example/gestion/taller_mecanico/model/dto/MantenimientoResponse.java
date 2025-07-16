package com.example.gestion.taller_mecanico.model.dto;

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
public class MantenimientoResponse {

    private Long id;
    private VehiculoResponse vehiculo;
    private ServicioResponse servicio;
    private TrabajadorResponse trabajador;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String observacionesCliente;
    private String observacionesTrabajador;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private List<MantenimientoProductoResponse> productosUsados;
}
