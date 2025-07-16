package com.example.gestion.taller_mecanico.exceptions;

public class TallerNotFoundException extends RuntimeException {
    public TallerNotFoundException(String message) {
        super(message);
    }
}
