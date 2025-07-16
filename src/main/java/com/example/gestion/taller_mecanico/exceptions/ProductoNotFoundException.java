package com.example.gestion.taller_mecanico.exceptions;

public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String message) {
        super(message);
    }
}
