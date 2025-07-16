package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrabajadorSpecification {

    public static Specification<Trabajador> filterTrabajadores(
            String search,
            String especialidad,
            Long tallerId,
            String rol,
            LocalDateTime fechaCreacionDesde,
            LocalDateTime fechaCreacionHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                Join<Trabajador, Usuario> usuarioJoin = root.join("usuario");
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("nombreCompleto")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("correo")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("username")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("especialidad")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (especialidad != null && !especialidad.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("especialidad")), especialidad.toLowerCase()));
            }

            if (tallerId != null) {
                Join<Trabajador, Taller> tallerJoin = root.join("taller");
                predicates.add(criteriaBuilder.equal(tallerJoin.get("id"), tallerId));
            }

            if (rol != null && !rol.trim().isEmpty()) {
                Join<Trabajador, Usuario> usuarioJoin = root.join("usuario");
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(usuarioJoin.get("rol")), rol.toLowerCase()));
            }

            if (fechaCreacionDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaCreacionDesde));
            }

            if (fechaCreacionHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaCreacion"), fechaCreacionHasta));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
