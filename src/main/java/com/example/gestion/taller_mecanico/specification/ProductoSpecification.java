package com.example.gestion.taller_mecanico.specification;

import com.example.gestion.taller_mecanico.model.entity.Producto;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.CategoriaProducto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecification {

    public static Specification<Producto> filterProductos(
            String search,
            Long tallerId,
            Long categoriaId,
            BigDecimal minPrecio,
            BigDecimal maxPrecio,
            Integer minStock,
            Integer maxStock) {

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
                Join<Producto, Taller> tallerJoin = root.join("taller");
                predicates.add(criteriaBuilder.equal(tallerJoin.get("id"), tallerId));
            }

            if (categoriaId != null) {
                Join<Producto, CategoriaProducto> categoriaJoin = root.join("categoria");
                predicates.add(criteriaBuilder.equal(categoriaJoin.get("id"), categoriaId));
            }

            if (minPrecio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), minPrecio));
            }

            if (maxPrecio != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), maxPrecio));
            }

            if (minStock != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stock"), minStock));
            }

            if (maxStock != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stock"), maxStock));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
