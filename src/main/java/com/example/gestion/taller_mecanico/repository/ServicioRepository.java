package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Long>, JpaSpecificationExecutor<Servicio> {
    Optional<Servicio> findByNombreAndTallerId(String nombre, Long tallerId);
}