package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlertaRepository extends JpaRepository<Alerta, Long>, JpaSpecificationExecutor<Alerta> {
}
