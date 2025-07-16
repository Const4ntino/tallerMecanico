package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Mantenimiento;
import com.example.gestion.taller_mecanico.utils.enums.MantenimientoEstado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long>, JpaSpecificationExecutor<Mantenimiento> {
    // Método para encontrar mantenimientos completados que no tienen una factura asociada
    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = :estado AND m.id NOT IN (SELECT f.mantenimiento.id FROM Factura f)")
    Page<Mantenimiento> findCompletedAndUnbilledMantenimientos(@Param("estado") MantenimientoEstado estado, Pageable pageable);

    long countByFechaCreacionBetween(LocalDateTime start, LocalDateTime end);
    long count();
}
