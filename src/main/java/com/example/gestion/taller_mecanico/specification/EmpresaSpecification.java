package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Empresa;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmpresaSpecification {

    public static Specification<Empresa> filterEmpresas(String search, String ruc, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Búsqueda general por razón social, ruc o correo
            if (search != null && !search.isEmpty()) {
                String searchLike = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("razon")), searchLike),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ruc")), searchLike),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("correo")), searchLike)
                ));
            }

            // Filtro por RUC específico
            if (ruc != null && !ruc.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("ruc"), ruc));
            }

            // Filtro por rango de fecha de creación
            if (fechaCreacionDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fechaCreacionDesde));
            }

            if (fechaCreacionHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), fechaCreacionHasta));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
