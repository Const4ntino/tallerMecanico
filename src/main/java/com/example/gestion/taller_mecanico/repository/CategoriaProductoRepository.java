package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long>, JpaSpecificationExecutor<CategoriaProducto> {
    Optional<CategoriaProducto> findByNombre(String nombre);
}