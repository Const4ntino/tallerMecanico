package com.example.gestion.taller_mecanico.utils.enums;

import lombok.Getter;

@Getter
public enum ErrorCatalog {

    USUARIO_NO_ENCONTRADO("ERROR_USUARIO_01", "Usuario no encontrado"),
    USUARIO_DATOS_INVALIDOS("ERROR_USUARIO_02", "Datos inválidos para Usuario"),

    TALLER_NO_ENCONTRADO("ERROR_TALLER_01", "Taller no encontrado"),
    TALLER_DATOS_INVALIDOS("ERROR_TALLER_02", "Datos inválidos para Taller"),

    CLIENTE_NO_ENCONTRADO("ERROR_CLIENTE_01", "Cliente no encontrado"),
    CLIENTE_DATOS_INVALIDOS("ERROR_CLIENTE_02", "Datos inválidos para Cliente"),

    TRABAJADOR_NO_ENCONTRADO("ERROR_TRABAJADOR_01", "Trabajador no encontrado"),
    TRABAJADOR_DATOS_INVALIDOS("ERROR_TRABAJADOR_02", "Datos inválidos para Trabajador"),

    VEHICULO_NO_ENCONTRADO("ERROR_VEHICULO_01", "Vehículo no encontrado"),
    VEHICULO_DATOS_INVALIDOS("ERROR_VEHICULO_02", "Datos inválidos para Vehículo"),

    CATEGORIA_PRODUCTO_NO_ENCONTRADA("ERROR_CATEGORIA_PRODUCTO_01", "Categoría de producto no encontrada"),
    CATEGORIA_PRODUCTO_DATOS_INVALIDOS("ERROR_CATEGORIA_PRODUCTO_02", "Datos inválidos para Categoría de Producto"),

    PRODUCTO_NO_ENCONTRADO("ERROR_PRODUCTO_01", "Producto no encontrado"),
    PRODUCTO_DATOS_INVALIDOS("ERROR_PRODUCTO_02", "Datos inválidos para Producto"),
    PRODUCTO_STOCK_INSUFICIENTE("ERROR_PRODUCTO_03", "Stock insuficiente para el producto"),

    SERVICIO_NO_ENCONTRADO("ERROR_SERVICIO_01", "Servicio no encontrado"),
    SERVICIO_DATOS_INVALIDOS("ERROR_SERVICIO_02", "Datos inválidos para Servicio"),

    MANTENIMIENTO_NO_ENCONTRADO("ERROR_MANTENIMIENTO_01", "Mantenimiento no encontrado"),
    MANTENIMIENTO_DATOS_INVALIDOS("ERROR_MANTENIMIENTO_02", "Datos inválidos para Mantenimiento"),

    MANTENIMIENTO_PRODUCTO_NO_ENCONTRADO("ERROR_MANTENIMIENTO_PRODUCTO_01", "Producto de Mantenimiento no encontrado"),
    MANTENIMIENTO_PRODUCTO_DATOS_INVALIDOS("ERROR_MANTENIMIENTO_PRODUCTO_02", "Datos inválidos para Producto de Mantenimiento"),

    FACTURA_NO_ENCONTRADA("ERROR_FACTURA_01", "Factura no encontrada"),
    FACTURA_DATOS_INVALIDOS("ERROR_FACTURA_02", "Datos inválidos para Factura"),

    GALERIA_NO_ENCONTRADA("ERROR_GALERIA_01", "Galería no encontrada"),
    GALERIA_DATOS_INVALIDOS("ERROR_GALERIA_02", "Datos inválidos para Galería"),

    ALERTA_NO_ENCONTRADA("ERROR_ALERTA_01", "Alerta no encontrada"),
    ALERTA_DATOS_INVALIDOS("ERROR_ALERTA_02", "Datos inválidos para Alerta"),

    ERROR_GENERICO("ERROR_GENERICO", "Un error inesperado a ocurrido");

    private final String codigo;
    private final String mensaje;

    ErrorCatalog(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }
}
