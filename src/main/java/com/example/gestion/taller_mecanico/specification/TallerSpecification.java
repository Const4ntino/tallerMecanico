package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Taller;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TallerSpecification {

    public static Specification<Taller> filterTalleres(
            String search,
            String ciudad,
            String estado,
            LocalDateTime fechaCreacionDesde,
            LocalDateTime fechaCreacionHasta,
            LocalDateTime fechaActualizacionDesde,
            LocalDateTime fechaActualizacionHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ciudad")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (ciudad != null && !ciudad.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("ciudad")), ciudad.toLowerCase()));
            }

            if (estado != null && !estado.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("estado")), estado.toLowerCase()));
            }

            if (fechaCreacionDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaCreacionDesde));
            }

            if (fechaCreacionHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaCreacion"), fechaCreacionHasta));
            }

            if (fechaActualizacionDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaActualizacion"), fechaActualizacionDesde));
            }

            if (fechaActualizacionHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaActualizacion"), fechaActualizacionHasta));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
