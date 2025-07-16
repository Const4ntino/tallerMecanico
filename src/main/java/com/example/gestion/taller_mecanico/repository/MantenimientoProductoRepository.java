package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.MantenimientoProducto;
import com.example.gestion.taller_mecanico.model.entity.MantenimientoProductoId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MantenimientoProductoRepository extends JpaRepository<MantenimientoProducto, MantenimientoProductoId>, JpaSpecificationExecutor<MantenimientoProducto> {
    List<MantenimientoProducto> findByMantenimientoId(Long mantenimientoId);
}
