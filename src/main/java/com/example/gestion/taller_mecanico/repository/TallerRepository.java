package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TallerRepository extends JpaRepository<Taller, Long>, JpaSpecificationExecutor<Taller> {
    Optional<Taller> findByNombre(String nombre);
}