package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Servicio;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ServicioSpecification {

    public static Specification<Servicio> filterServicios(
            String search,
            Long tallerId,
            BigDecimal minPrecioBase,
            BigDecimal maxPrecioBase,
            BigDecimal minDuracionEstimadaHoras,
            BigDecimal maxDuracionEstimadaHoras) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (tallerId != null) {
                Join<Servicio, Taller> tallerJoin = root.join("taller");
                predicates.add(criteriaBuilder.equal(tallerJoin.get("id"), tallerId));
            }

            if (minPrecioBase != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precioBase"), minPrecioBase));
            }

            if (maxPrecioBase != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precioBase"), maxPrecioBase));
            }

            if (minDuracionEstimadaHoras != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("duracionEstimadaHoras"), minDuracionEstimadaHoras));
            }

            if (maxDuracionEstimadaHoras != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("duracionEstimadaHoras"), maxDuracionEstimadaHoras));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
