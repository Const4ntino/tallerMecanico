package com.example.gestion.taller_mecanico.exceptions;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
