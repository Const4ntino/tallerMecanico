package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Galeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GaleriaRepository extends JpaRepository<Galeria, Long>, JpaSpecificationExecutor<Galeria> {
}
