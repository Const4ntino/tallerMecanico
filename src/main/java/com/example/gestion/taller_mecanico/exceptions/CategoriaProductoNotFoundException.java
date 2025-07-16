package com.example.gestion.taller_mecanico.exceptions;

public class CategoriaProductoNotFoundException extends RuntimeException {
    public CategoriaProductoNotFoundException(String message) {
        super(message);
    }
}
