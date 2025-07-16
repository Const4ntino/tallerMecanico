package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    Optional<Vehiculo> findByPlaca(String placa);
}