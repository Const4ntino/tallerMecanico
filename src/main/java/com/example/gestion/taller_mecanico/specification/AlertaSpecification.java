package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Alerta;
import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaSpecification {

    public static Specification<Alerta> filterAlertas(
            String search,
            Long vehiculoId,
            Long clienteId,
            Long tallerId,
            String tipo,
            String estado,
            LocalDateTime fechaCreacionDesde,
            LocalDateTime fechaCreacionHasta) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("mensaje")), "%" + lowerCaseSearch + "%"));
            }

            if (vehiculoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("vehiculo").get("id"), vehiculoId));
            }

            if (clienteId != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("id"), clienteId));
            }

            if (tallerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("taller").get("id"), tallerId));
            }

            if (tipo != null && !tipo.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("tipo")), tipo.toLowerCase()));
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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
