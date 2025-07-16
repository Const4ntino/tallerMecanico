package com.example.gestion.taller_mecanico.exceptions;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(String message) {
        super(message);
    }
}
