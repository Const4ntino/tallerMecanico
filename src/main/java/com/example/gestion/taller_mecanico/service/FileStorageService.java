package com.example.gestion.taller_mecanico.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDirFacturas = System.getProperty("user.dir") + "/uploads/facturas";
    private final String uploadDirImagenes = System.getProperty("user.dir") + "/uploads/imagenes";

    public String storeFacturaPdf(byte[] pdfContent, String originalFilename) throws IOException {
        File directorio = new File(uploadDirFacturas);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        String nombreArchivo = UUID.randomUUID() + "_" + originalFilename;
        Path rutaArchivo = Paths.get(uploadDirFacturas, nombreArchivo);
        Files.write(rutaArchivo, pdfContent); // Escribe los bytes directamente
        return "/uploads/facturas/" + nombreArchivo;
    }

    public String storeImage(byte[] imageContent, String originalFilename) throws IOException {
        File directorio = new File(uploadDirImagenes);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        String nombreArchivo = UUID.randomUUID() + "_" + originalFilename;
        Path rutaArchivo = Paths.get(uploadDirImagenes, nombreArchivo);
        Files.write(rutaArchivo, imageContent);
        return "/uploads/imagenes/" + nombreArchivo;
    }
}
