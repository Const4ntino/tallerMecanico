package com.example.gestion.taller_mecanico.model.entity;

import com.example.gestion.taller_mecanico.utils.enums.TallerEstado;
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
@Table(name = "talleres")
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 100)
    private String ciudad;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TallerEstado estado;

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
