package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    Optional<Producto> findByNombreAndTallerId(String nombre, Long tallerId);
}