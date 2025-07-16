package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Mantenimiento;
import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import com.example.gestion.taller_mecanico.model.entity.Servicio;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoSpecification {

    public static Specification<Mantenimiento> filterMantenimientos(
            String search,
            Long vehiculoId,
            Long servicioId,
            Long trabajadorId,
            String estado,
            LocalDateTime fechaInicioDesde,
            LocalDateTime fechaInicioHasta,
            LocalDateTime fechaFinDesde,
            LocalDateTime fechaFinHasta,
            Long clienteId // Para filtrar mantenimientos de un cliente específico
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                Join<Mantenimiento, Vehiculo> vehiculoJoin = root.join("vehiculo");
                Join<Mantenimiento, Servicio> servicioJoin = root.join("servicio");
                Join<Vehiculo, Cliente> clienteJoin = vehiculoJoin.join("cliente");
                Join<Cliente, Usuario> usuarioJoin = clienteJoin.join("usuario");

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(vehiculoJoin.get("placa")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(servicioJoin.get("nombre")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("observacionesCliente")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("observacionesTrabajador")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("nombreCompleto")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (vehiculoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("vehiculo").get("id"), vehiculoId));
            }

            if (servicioId != null) {
                predicates.add(criteriaBuilder.equal(root.get("servicio").get("id"), servicioId));
            }

            if (trabajadorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("trabajador").get("id"), trabajadorId));
            }

            if (estado != null && !estado.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("estado")), estado.toLowerCase()));
            }

            if (fechaInicioDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaInicio"), fechaInicioDesde));
            }

            if (fechaInicioHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaInicio"), fechaInicioHasta));
            }

            if (fechaFinDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaFin"), fechaFinDesde));
            }

            if (fechaFinHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaFin"), fechaFinHasta));
            }

            if (clienteId != null) {
                Join<Mantenimiento, Vehiculo> vehiculoJoin = root.join("vehiculo");
                predicates.add(criteriaBuilder.equal(vehiculoJoin.get("cliente").get("id"), clienteId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
