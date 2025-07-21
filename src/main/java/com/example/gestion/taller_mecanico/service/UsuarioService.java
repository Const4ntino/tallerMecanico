package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.UsuarioClienteRequest;
import com.example.gestion.taller_mecanico.model.dto.UsuarioRequest;
import com.example.gestion.taller_mecanico.model.dto.UsuarioResponse;
import com.example.gestion.taller_mecanico.model.dto.UsuarioTrabajadorRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> findAll();
    UsuarioResponse findById(Long id);
    UsuarioResponse save(UsuarioRequest usuarioRequest);
    UsuarioResponse update(Long id, UsuarioRequest usuarioRequest);
    UsuarioResponse createUsuarioCliente(UsuarioClienteRequest request);
    UsuarioResponse createUsuarioTrabajador(UsuarioTrabajadorRequest request);
    void deleteById(Long id);
    Page<UsuarioResponse> findUsuariosByFilters(String search, String rol, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, LocalDateTime fechaActualizacionDesde, LocalDateTime fechaActualizacionHasta, Pageable pageable);
    List<UsuarioResponse> findTrabajadoresNoAsignados();
    List<UsuarioResponse> findClientesNoAsignados();
}