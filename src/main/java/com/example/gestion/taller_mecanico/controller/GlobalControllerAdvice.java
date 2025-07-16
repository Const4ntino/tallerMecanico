package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.ErrorResponse;
import com.example.gestion.taller_mecanico.exceptions.CategoriaProductoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.ClienteNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.ProductoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.MantenimientoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.MantenimientoProductoNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.ServicioNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.UsuarioNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.AlertaNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.GaleriaNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.FacturaNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.InsufficientStockException;
import com.example.gestion.taller_mecanico.exceptions.VehiculoNotFoundException;
import com.example.gestion.taller_mecanico.utils.enums.ErrorCatalog;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.example.gestion.taller_mecanico.utils.enums.ErrorCatalog.*;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ErrorResponse handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(USUARIO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TallerNotFoundException.class)
    public ErrorResponse handleTallerNotFoundException(TallerNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(TALLER_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ClienteNotFoundException.class)
    public ErrorResponse handleClienteNotFoundException(ClienteNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(CLIENTE_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrabajadorNotFoundException.class)
    public ErrorResponse handleTrabajadorNotFoundException(TrabajadorNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(TRABAJADOR_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(VehiculoNotFoundException.class)
    public ErrorResponse handleVehiculoNotFoundException(VehiculoNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(VEHICULO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CategoriaProductoNotFoundException.class)
    public ErrorResponse handleCategoriaProductoNotFoundException(CategoriaProductoNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(CATEGORIA_PRODUCTO_NO_ENCONTRADA.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductoNotFoundException.class)
    public ErrorResponse handleProductoNotFoundException(ProductoNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(PRODUCTO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ServicioNotFoundException.class)
    public ErrorResponse handleServicioNotFoundException(ServicioNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(SERVICIO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MantenimientoNotFoundException.class)
    public ErrorResponse handleMantenimientoNotFoundException(MantenimientoNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(MANTENIMIENTO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MantenimientoProductoNotFoundException.class)
    public ErrorResponse handleMantenimientoProductoNotFoundException(MantenimientoProductoNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(MANTENIMIENTO_PRODUCTO_NO_ENCONTRADO.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientStockException.class)
    public ErrorResponse handleInsufficientStockException(InsufficientStockException ex) {
        return ErrorResponse.builder()
                .codigo(PRODUCTO_STOCK_INSUFICIENTE.getCodigo())
                .status(HttpStatus.BAD_REQUEST)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FacturaNotFoundException.class)
    public ErrorResponse handleFacturaNotFoundException(FacturaNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(FACTURA_NO_ENCONTRADA.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GaleriaNotFoundException.class)
    public ErrorResponse handleGaleriaNotFoundException(GaleriaNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(GALERIA_NO_ENCONTRADA.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AlertaNotFoundException.class)
    public ErrorResponse handleAlertaNotFoundException(AlertaNotFoundException ex) {
        return ErrorResponse.builder()
                .codigo(ALERTA_NO_ENCONTRADA.getCodigo())
                .status(HttpStatus.NOT_FOUND)
                .mensaje(ex.getMessage())
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();

        String nombreObjeto = result.getObjectName();

        ErrorCatalog errorCatalogo;

        if (nombreObjeto.toLowerCase().contains("usuario")) {
            errorCatalogo = USUARIO_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("taller")) {
            errorCatalogo = TALLER_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("cliente")) {
            errorCatalogo = CLIENTE_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("trabajador")) {
            errorCatalogo = TRABAJADOR_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("vehiculo")) {
            errorCatalogo = VEHICULO_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("categoriaproducto")) {
            errorCatalogo = CATEGORIA_PRODUCTO_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("producto")) {
            errorCatalogo = PRODUCTO_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("servicio")) {
            errorCatalogo = SERVICIO_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("mantenimiento")) {
            errorCatalogo = MANTENIMIENTO_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("galeria")) {
            errorCatalogo = GALERIA_DATOS_INVALIDOS;
        } else if (nombreObjeto.toLowerCase().contains("alerta")) {
            errorCatalogo = ALERTA_DATOS_INVALIDOS;
        } else {
            errorCatalogo = ERROR_GENERICO;
        }

        return ErrorResponse.builder()
                .codigo(errorCatalogo.getCodigo())
                .status(HttpStatus.BAD_REQUEST)
                .mensaje(errorCatalogo.getMensaje())
                .detalleMensaje(result.getFieldErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList()))
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handlerInternalServerError(Exception exception) {
        return ErrorResponse.builder()
                .codigo(ERROR_GENERICO.getCodigo())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .mensaje(ERROR_GENERICO.getMensaje())
                .detalleMensaje(Collections.singletonList(exception.getMessage()))
                .marcaDeTiempo(LocalDateTime.now())
                .build();
    }
}
