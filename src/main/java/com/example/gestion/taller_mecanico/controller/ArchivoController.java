package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.service.ArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
public class ArchivoController {

    private final ArchivoService archivoService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping("/subir-factura")
    public ResponseEntity<String> subirFactura(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        try {
            String urlRelativa = archivoService.subirFactura(archivo);
            return ResponseEntity.ok(urlRelativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @PostMapping("/subir-imagen")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        try {
            String urlRelativa = archivoService.subirImagen(archivo);
            return ResponseEntity.ok(urlRelativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/subir-logo")
    public ResponseEntity<String> subirLogo(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        try {
            String urlRelativa = archivoService.subirLogo(archivo);
            return ResponseEntity.ok(urlRelativa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
