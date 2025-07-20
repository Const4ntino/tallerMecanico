package com.example.gestion.taller_mecanico.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Servicio para la gestión de archivos (subida, almacenamiento, etc.)
 */
public interface ArchivoService {

    /**
     * Sube un archivo de factura al sistema
     * @param archivo El archivo a subir
     * @return URL relativa donde se guardó el archivo
     * @throws IOException Si ocurre un error durante la subida
     */
    String subirFactura(MultipartFile archivo) throws IOException;

    /**
     * Sube una imagen al sistema
     * @param archivo El archivo de imagen a subir
     * @return URL relativa donde se guardó la imagen
     * @throws IOException Si ocurre un error durante la subida
     */
    String subirImagen(MultipartFile archivo) throws IOException;

    /**
     * Sube un logo al sistema, reemplazando cualquier logo existente
     * @param archivo El archivo de logo a subir
     * @return URL relativa donde se guardó el logo
     * @throws IOException Si ocurre un error durante la subida
     */
    String subirLogo(MultipartFile archivo) throws IOException;
}
