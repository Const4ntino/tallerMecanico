package com.example.gestion.taller_mecanico.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mantenimiento_productos")
public class MantenimientoProducto {

    @EmbeddedId
    private MantenimientoProductoId id;

    @ManyToOne
    @MapsId("mantenimientoId")
    @JoinColumn(name = "mantenimiento_id")
    private Mantenimiento mantenimiento;

    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "cantidad_usada", nullable = false)
    private Integer cantidadUsada;

    @Column(name = "precio_en_uso", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioEnUso;
}
