package com.example.gestion.taller_mecanico.repository;

import com.example.gestion.taller_mecanico.model.entity.Factura;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaRepository extends JpaRepository<Factura, Long>, JpaSpecificationExecutor<Factura> {
    @Query("SELECT f.total, f.fechaEmision FROM Factura f WHERE f.fechaEmision BETWEEN :startDate AND :endDate")
    List<Object[]> findTotalIngresosAndGroupByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT f.total, f.fechaEmision FROM Factura f")
    List<Object[]> findTotalIngresosAndGroupByDate();
    
    // Verificar si existe una factura para un mantenimiento específico
    boolean existsByMantenimientoId(Long mantenimientoId);
}
