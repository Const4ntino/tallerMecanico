package com.example.gestion.taller_mecanico.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaResponse {
    private Long id;
    private String razon;
    private String descripcion;
    private String ruc;
    private String correo;
    private String telefono;
    private String direccion;
    private String logo;
}

