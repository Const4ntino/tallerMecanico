package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.ClienteRequest;
import com.example.gestion.taller_mecanico.model.dto.ClienteResponse;
import com.example.gestion.taller_mecanico.model.dto.UsuarioClienteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ClienteService {
    List<ClienteResponse> findAll();
    ClienteResponse findById(Long id);
    ClienteResponse findByUsuarioId(Long usuarioId);
    ClienteResponse save(ClienteRequest clienteRequest);
    ClienteResponse update(Long id, ClienteRequest clienteRequest);
    void deleteById(Long id);
    ClienteResponse getMisDatos();
    ClienteResponse updateMisDatos(UsuarioClienteRequest usuarioClienteRequest);
    Page<ClienteResponse> findClientesByFilters(String search, Long tallerAsignadoId, String telefono, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable);
}