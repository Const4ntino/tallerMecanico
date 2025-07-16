package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.FacturaRequest;
import com.example.gestion.taller_mecanico.model.dto.FacturaResponse;
import com.example.gestion.taller_mecanico.model.entity.Factura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MantenimientoMapper.class, ClienteMapper.class, TallerMapper.class})
public interface FacturaMapper {

    FacturaResponse toFacturaResponse(Factura factura);

    @Mapping(source = "mantenimientoId", target = "mantenimiento.id")
    @Mapping(source = "clienteId", target = "cliente.id")
    @Mapping(source = "tallerId", target = "taller.id")
    Factura toFactura(FacturaRequest facturaRequest);
}
