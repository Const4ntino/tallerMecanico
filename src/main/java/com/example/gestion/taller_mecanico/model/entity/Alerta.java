package com.example.gestion.taller_mecanico.model.entity;

import com.example.gestion.taller_mecanico.utils.enums.AlertaEstado;
import com.example.gestion.taller_mecanico.utils.enums.AlertaTipo;
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
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "taller_id")
    private Taller taller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AlertaTipo tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertaEstado estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
