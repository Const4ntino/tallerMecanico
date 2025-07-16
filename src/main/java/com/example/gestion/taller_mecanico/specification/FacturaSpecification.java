package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Factura;
import com.example.gestion.taller_mecanico.model.entity.Mantenimiento;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FacturaSpecification {

    public static Specification<Factura> filterFacturas(
            String search,
            Long mantenimientoId,
            Long clienteId,
            Long tallerId,
            LocalDateTime fechaEmisionDesde,
            LocalDateTime fechaEmisionHasta,
            BigDecimal minTotal,
            BigDecimal maxTotal) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                Join<Factura, Mantenimiento> mantenimientoJoin = root.join("mantenimiento");
                Join<Factura, Cliente> clienteJoin = root.join("cliente");
                Join<Factura, Taller> tallerJoin = root.join("taller");

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(mantenimientoJoin.get("observacionesCliente")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(mantenimientoJoin.get("observacionesTrabajador")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(clienteJoin.get("usuario").get("nombreCompleto")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(tallerJoin.get("nombre")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (mantenimientoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("mantenimiento").get("id"), mantenimientoId));
            }

            if (clienteId != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("id"), clienteId));
            }

            if (tallerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("taller").get("id"), tallerId));
            }

            if (fechaEmisionDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaEmision"), fechaEmisionDesde));
            }

            if (fechaEmisionHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaEmision"), fechaEmisionHasta));
            }

            if (minTotal != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("total"), minTotal));
            }

            if (maxTotal != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("total"), maxTotal));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
