package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Galeria;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GaleriaSpecification {

    public static Specification<Galeria> filterGalerias(
            String search,
            Long tallerId,
            String tipo) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (tallerId != null) {
                Join<Galeria, Taller> tallerJoin = root.join("taller");
                predicates.add(criteriaBuilder.equal(tallerJoin.get("id"), tallerId));
            }

            if (tipo != null && !tipo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("tipo")), tipo.toLowerCase()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
