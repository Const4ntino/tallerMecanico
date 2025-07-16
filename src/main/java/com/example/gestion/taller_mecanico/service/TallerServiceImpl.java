package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.mapper.TallerMapper;
import com.example.gestion.taller_mecanico.model.dto.TallerRequest;
import com.example.gestion.taller_mecanico.model.dto.TallerResponse;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.specification.TallerSpecification;
import com.example.gestion.taller_mecanico.utils.enums.TallerEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TallerServiceImpl implements TallerService {

    private final TallerRepository tallerRepository;
    private final TallerMapper tallerMapper;

    @Override
    public List<TallerResponse> findAll() {
        return tallerRepository.findAll().stream()
                .map(tallerMapper::toTallerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TallerResponse findById(Long id) {
        return tallerRepository.findById(id)
                .map(tallerMapper::toTallerResponse)
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + id));
    }

    @Override
    public TallerResponse findByNombre(String nombre) {
        return tallerRepository.findByNombre(nombre)
                .map(tallerMapper::toTallerResponse)
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con nombre: " + nombre));
    }

    @Override
    public TallerResponse save(TallerRequest tallerRequest) {
        Taller taller = Taller.builder()
                .nombre(tallerRequest.getNombre())
                .ciudad(tallerRequest.getCiudad())
                .direccion(tallerRequest.getDireccion())
                .logoUrl(tallerRequest.getLogoUrl())
                .estado(TallerEstado.valueOf(tallerRequest.getEstado().toUpperCase()))
                .build();
        return tallerMapper.toTallerResponse(tallerRepository.save(taller));
    }

    @Override
    public TallerResponse update(Long id, TallerRequest tallerRequest) {
        return tallerRepository.findById(id)
                .map(taller -> {
                    taller.setNombre(tallerRequest.getNombre());
                    taller.setCiudad(tallerRequest.getCiudad());
                    taller.setDireccion(tallerRequest.getDireccion());
                    taller.setLogoUrl(tallerRequest.getLogoUrl());
                    taller.setEstado(TallerEstado.valueOf(tallerRequest.getEstado().toUpperCase()));
                    return tallerMapper.toTallerResponse(tallerRepository.save(taller));
                }).orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!tallerRepository.existsById(id)) {
            throw new TallerNotFoundException("Taller no encontrado con ID: " + id);
        }
        tallerRepository.deleteById(id);
    }

    @Override
    public Page<TallerResponse> findTalleresByFilters(String search, String ciudad, String estado, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, LocalDateTime fechaActualizacionDesde, LocalDateTime fechaActualizacionHasta, Pageable pageable) {
        Specification<Taller> spec = TallerSpecification.filterTalleres(search, ciudad, estado, fechaCreacionDesde, fechaCreacionHasta, fechaActualizacionDesde, fechaActualizacionHasta);
        return tallerRepository.findAll(spec, pageable).map(tallerMapper::toTallerResponse);
    }
}