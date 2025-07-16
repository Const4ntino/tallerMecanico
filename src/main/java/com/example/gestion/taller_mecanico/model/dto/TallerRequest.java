package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TallerRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @Size(max = 100)
    private String ciudad;

    private String direccion;

    private String logoUrl;

    private String estado;
}
