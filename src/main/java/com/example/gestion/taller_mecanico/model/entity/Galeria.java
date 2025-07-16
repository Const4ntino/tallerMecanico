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
@Table(name = "galerias")
public class Galeria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @Column(name = "imagen_url", nullable = false, columnDefinition = "TEXT")
    private String imagenUrl;

    @Column(length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 50)
    private String tipo;
}
