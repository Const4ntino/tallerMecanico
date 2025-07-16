package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long>, JpaSpecificationExecutor<Trabajador> {
    Optional<Trabajador> findByUsuarioId(Long usuarioId);
}