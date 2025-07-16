package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecification {

    public static Specification<Usuario> filterUsuarios(
            String search,
            String rol,
            LocalDateTime fechaCreacionDesde,
            LocalDateTime fechaCreacionHasta,
            LocalDateTime fechaActualizacionDesde,
            LocalDateTime fechaActualizacionHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreCompleto")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("correo")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (rol != null && !rol.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("rol")), rol.toLowerCase()));
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
