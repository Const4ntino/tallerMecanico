package com.example.gestion.taller_mecanico.model.dto;

import jakarta.validation.constraints.Email;
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
public class UsuarioRequest {

    @NotBlank
    @Size(max = 150)
    private String nombreCompleto;

    @NotBlank
    @Size(max = 8)
    private String dni;

    @Email
    @Size(max = 100)
    private String correo;

    @NotBlank
    @Size(max = 50)
    private String username;

    private String contrasena;

    @NotBlank
    private String rol;
}
