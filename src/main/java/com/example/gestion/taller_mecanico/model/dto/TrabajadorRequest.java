package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrabajadorRequest {

    @NotNull
    private Long usuarioId;

    @Size(max = 100)
    private String especialidad;

    @NotNull
    private Long tallerId;
}
