package com.example.gestion.taller_mecanico.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MantenimientoProductoId implements Serializable {
    private Long mantenimientoId;
    private Long productoId;
}
