package com.example.gestion.taller_mecanico.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de archivos
 */
@Service
public class ArchivoServiceImpl implements ArchivoService {

    private final String uploadDirFacturas = System.getProperty("user.dir") + "/uploads/facturas";
    private final String uploadDirImagenes = System.getProperty("user.dir") + "/uploads/imagenes";

    @Override
    public String subirFactura(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
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
        return urlRelativa;
    }

    @Override
    public String subirImagen(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
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
        return urlRelativa;
    }

    @Override
    public String subirLogo(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
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

        return urlRelativa;
    }
}
