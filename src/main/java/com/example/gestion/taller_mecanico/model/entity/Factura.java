package com.example.gestion.taller_mecanico.model.entity;

import com.example.gestion.taller_mecanico.utils.enums.MetodoPago;
import com.example.gestion.taller_mecanico.utils.enums.TipoFactura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "mantenimiento_id", unique = true, nullable = false)
    private Mantenimiento mantenimiento;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "taller_id", nullable = false)
    private Taller taller;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    private String detalles;

    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;

    @Column(name = "codigo_factura", nullable = false, unique = true, updatable = false)
    private String codigoFactura;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private MetodoPago metodoPago;

    @Column(name = "nro_operacion")
    private String nroOperacion;

    @Column(name = "imagen_operacion", columnDefinition = "TEXT")
    private String imagenOperacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoFactura tipo;

    @PrePersist
    protected void onCreate() {
        fechaEmision = LocalDateTime.now();
    }
}
