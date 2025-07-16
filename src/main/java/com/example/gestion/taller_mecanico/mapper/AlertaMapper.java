package com.example.gestion.taller_mecanico.mapper;

import com.example.gestion.taller_mecanico.model.dto.AlertaRequest;
import com.example.gestion.taller_mecanico.model.dto.AlertaResponse;
import com.example.gestion.taller_mecanico.model.entity.Alerta;
import com.example.gestion.taller_mecanico.utils.enums.AlertaEstado;
import com.example.gestion.taller_mecanico.utils.enums.AlertaTipo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VehiculoMapper.class, ClienteMapper.class, TallerMapper.class})
public interface AlertaMapper {

    @Mapping(source = "tipo", target = "tipo")
    @Mapping(source = "estado", target = "estado")
    AlertaResponse toAlertaResponse(Alerta alerta);

    @Mapping(source = "vehiculoId", target = "vehiculo.id")
    @Mapping(source = "clienteId", target = "cliente.id")
    @Mapping(source = "tallerId", target = "taller.id")
    @Mapping(source = "tipo", target = "tipo")
    @Mapping(source = "estado", target = "estado")
    Alerta toAlerta(AlertaRequest alertaRequest);

    default String mapAlertaTipo(AlertaTipo tipo) {
        return tipo != null ? tipo.name() : null;
    }

    default AlertaTipo mapAlertaTipo(String tipo) {
        return tipo != null ? AlertaTipo.valueOf(tipo) : null;
    }

    default String mapAlertaEstado(AlertaEstado estado) {
        return estado != null ? estado.name() : null;
    }

    default AlertaEstado mapAlertaEstado(String estado) {
        return estado != null ? AlertaEstado.valueOf(estado) : null;
    }
}
