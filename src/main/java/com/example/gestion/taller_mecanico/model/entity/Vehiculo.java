package com.example.gestion.taller_mecanico.model.entity;

import com.example.gestion.taller_mecanico.utils.enums.VehiculoEstado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 15, unique = true)
    private String placa;

    @Column(length = 50)
    private String marca;

    @Column(length = 50)
    private String modelo;

    private Integer anio;

    @Column(length = 100)
    private String motor;

    @Column(name = "tipo_vehiculo", length = 50)
    private String tipoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehiculoEstado estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
