package com.example.gestion.taller_mecanico.exceptions;

public class MantenimientoNotFoundException extends RuntimeException {
    public MantenimientoNotFoundException(String message) {
        super(message);
    }
}
