package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.VehiculoClientRequest;
import com.example.gestion.taller_mecanico.model.dto.VehiculoRequest;
import com.example.gestion.taller_mecanico.model.dto.VehiculoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VehiculoService {
    List<VehiculoResponse> findAll();
    VehiculoResponse findById(Long id);
    VehiculoResponse findByPlaca(String placa);
    VehiculoResponse save(VehiculoRequest vehiculoRequest);
    VehiculoResponse update(Long id, VehiculoRequest vehiculoRequest);
    VehiculoResponse saveClientVehiculo(VehiculoClientRequest vehiculoClientRequest);
    void deleteById(Long id);
    Page<VehiculoResponse> findVehiculosByFilters(String search, Long clienteId, Long tallerAsignadoId, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Boolean excluirVehiculosEnMantenimiento, Pageable pageable);
    Page<VehiculoResponse> findMyVehiculosByFilters(String search, String estado, Boolean excluirVehiculosEnMantenimiento, Pageable pageable);
    VehiculoResponse updateClientVehiculo(Long id, VehiculoClientRequest vehiculoClientRequest);
    VehiculoResponse updateEstadoVehiculo(Long id, String estadoVehiculo);
}