package com.example.gestion.taller_mecanico.exceptions;

public class VehiculoNotFoundException extends RuntimeException {
    public VehiculoNotFoundException(String message) {
        super(message);
    }
}
