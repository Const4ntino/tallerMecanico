package com.example.gestion.taller_mecanico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final String uploadDirFacturas = System.getProperty("user.dir") + "/uploads/facturas";
    private final String uploadDirImagenes = System.getProperty("user.dir") + "/uploads/imagenes";

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @PostMapping("/subir-factura")
    public ResponseEntity<String> subirFactura(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        File directorio = new File(uploadDirFacturas);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = Paths.get(uploadDirFacturas, nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        String urlRelativa = "/uploads/facturas/" + nombreArchivo;
        System.out.println("Guardado en: " + rutaArchivo.toAbsolutePath());
        return ResponseEntity.ok(urlRelativa);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER', 'TRABAJADOR')")
    @PostMapping("/subir-imagen")
    public ResponseEntity<String> subirImagen(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        File directorio = new File(uploadDirImagenes);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path rutaArchivo = Paths.get(uploadDirImagenes, nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        String urlRelativa = "/uploads/imagenes/" + nombreArchivo;
        System.out.println("Guardado en: " + rutaArchivo.toAbsolutePath());
        return ResponseEntity.ok(urlRelativa);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/subir-logo")
    public ResponseEntity<String> subirLogo(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        File directorio = new File(uploadDirImagenes);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        // Elimina cualquier archivo existente llamado "logo.*"
        File[] archivosExistentes = directorio.listFiles((dir, name) -> name.startsWith("logo."));
        if (archivosExistentes != null) {
            for (File archivoExistente : archivosExistentes) {
                archivoExistente.delete();
            }
        }

        // Obtener extensión del archivo actual
        String extension = FilenameUtils.getExtension(archivo.getOriginalFilename());
        String nombreArchivo = "logo." + extension;

        // Guardar nuevo archivo
        Path rutaArchivo = Paths.get(uploadDirImagenes, nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        String urlRelativa = "/uploads/imagenes/" + nombreArchivo;
        System.out.println("Logo guardado en: " + rutaArchivo.toAbsolutePath());

        return ResponseEntity.ok(urlRelativa);
    }
}
