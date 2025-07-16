package com.example.gestion.taller_mecanico.exceptions;

public class FacturaNotFoundException extends RuntimeException {
    public FacturaNotFoundException(String message) {
        super(message);
    }
}
