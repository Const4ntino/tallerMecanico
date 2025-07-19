package com.example.gestion.taller_mecanico.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class EmpresaRequest {
    @NotBlank
    @Size(max = 100)
    private String razon;

    private String descripcion;

    @NotBlank
    @Size(min = 11, max = 11)
    private String ruc;

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @NotBlank
    @Size(min = 9, max = 9)
    private String telefono;

    @NotBlank
    private String direccion;

    private String logo;
}
