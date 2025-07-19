package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Vehiculo;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Mantenimiento;
import com.example.gestion.taller_mecanico.utils.enums.MantenimientoEstado;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VehiculoSpecification {

    public static Specification<Vehiculo> filterVehiculos(
            String search,
            Long clienteId,
            Long tallerAsignadoId,
            String estado,
            LocalDateTime fechaCreacionDesde,
            LocalDateTime fechaCreacionHasta,
            Boolean excluirVehiculosEnMantenimiento) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String lowerCaseSearch = search.toLowerCase();
                Join<Vehiculo, Cliente> clienteJoin = root.join("cliente");
                Join<Cliente, Usuario> usuarioJoin = clienteJoin.join("usuario");

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("placa")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("modelo")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("motor")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tipoVehiculo")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("nombreCompleto")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("correo")), "%" + lowerCaseSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(usuarioJoin.get("username")), "%" + lowerCaseSearch + "%")
                ));
            }

            if (clienteId != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("id"), clienteId));
            }

            if (tallerAsignadoId != null) {
                Join<Vehiculo, Cliente> clienteJoin = root.join("cliente");
                Join<Cliente, Taller> tallerJoin = clienteJoin.join("tallerAsignado");
                predicates.add(criteriaBuilder.equal(tallerJoin.get("id"), tallerAsignadoId));
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
            
            // Excluir vehículos que están en mantenimientos con estados específicos
            if (Boolean.TRUE.equals(excluirVehiculosEnMantenimiento)) {
                Subquery<Long> mantenimientoSubquery = query.subquery(Long.class);
                jakarta.persistence.criteria.Root<Mantenimiento> mantenimientoRoot = mantenimientoSubquery.from(Mantenimiento.class);
                
                List<String> estadosExcluidos = Arrays.asList(
                    MantenimientoEstado.SOLICITADO.toString(),
                    MantenimientoEstado.EN_PROCESO.toString(),
                    MantenimientoEstado.PENDIENTE.toString()
                );
                
                mantenimientoSubquery.select(mantenimientoRoot.get("vehiculo").get("id"))
                    .where(
                        criteriaBuilder.and(
                            criteriaBuilder.equal(mantenimientoRoot.get("vehiculo"), root),
                            mantenimientoRoot.get("estado").in(estadosExcluidos)
                        )
                    );
                
                predicates.add(criteriaBuilder.not(criteriaBuilder.exists(mantenimientoSubquery)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
