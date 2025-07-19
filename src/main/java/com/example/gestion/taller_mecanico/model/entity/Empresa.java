package com.example.gestion.taller_mecanico.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String razon;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 11, nullable = false)
    private String ruc;

    @Column(length = 100, nullable = false)
    private String correo;

    @Column(length = 9, nullable = false)
    private String telefono;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String direccion;

    @Column(columnDefinition = "TEXT")
    private String logo;
}
