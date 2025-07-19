package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.EmpresaNotFoundException;
import com.example.gestion.taller_mecanico.mapper.EmpresaMapper;
import com.example.gestion.taller_mecanico.model.dto.EmpresaRequest;
import com.example.gestion.taller_mecanico.model.dto.EmpresaResponse;
import com.example.gestion.taller_mecanico.model.entity.Empresa;
import com.example.gestion.taller_mecanico.repository.EmpresaRepository;
import com.example.gestion.taller_mecanico.specification.EmpresaSpecification;
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
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    public List<EmpresaResponse> findAll() {
        return empresaRepository.findAll().stream()
                .map(empresaMapper::toEmpresaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaResponse findById(Long id) {
        return empresaRepository.findById(id)
                .map(empresaMapper::toEmpresaResponse)
                .orElseThrow(() -> new EmpresaNotFoundException("Empresa no encontrada con ID: " + id));
    }

    @Override
    public EmpresaResponse findByRazon(String razon) {
        return empresaRepository.findByRazon(razon)
                .map(empresaMapper::toEmpresaResponse)
                .orElseThrow(() -> new EmpresaNotFoundException("Empresa no encontrada con razón social: " + razon));
    }

    @Override
    public EmpresaResponse findByRuc(String ruc) {
        return empresaRepository.findByRuc(ruc)
                .map(empresaMapper::toEmpresaResponse)
                .orElseThrow(() -> new EmpresaNotFoundException("Empresa no encontrada con RUC: " + ruc));
    }

    @Override
    public EmpresaResponse save(EmpresaRequest empresaRequest) {
        Empresa empresa = Empresa.builder()
                .razon(empresaRequest.getRazon())
                .descripcion(empresaRequest.getDescripcion())
                .ruc(empresaRequest.getRuc())
                .correo(empresaRequest.getCorreo())
                .telefono(empresaRequest.getTelefono())
                .direccion(empresaRequest.getDireccion())
                .logo(empresaRequest.getLogo())
                .build();
        return empresaMapper.toEmpresaResponse(empresaRepository.save(empresa));
    }

    @Override
    public EmpresaResponse update(Long id, EmpresaRequest empresaRequest) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setRazon(empresaRequest.getRazon());
                    empresa.setDescripcion(empresaRequest.getDescripcion());
                    empresa.setRuc(empresaRequest.getRuc());
                    empresa.setCorreo(empresaRequest.getCorreo());
                    empresa.setTelefono(empresaRequest.getTelefono());
                    empresa.setDireccion(empresaRequest.getDireccion());
                    empresa.setLogo(empresaRequest.getLogo());
                    return empresaMapper.toEmpresaResponse(empresaRepository.save(empresa));
                }).orElseThrow(() -> new EmpresaNotFoundException("Empresa no encontrada con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new EmpresaNotFoundException("Empresa no encontrada con ID: " + id);
        }
        empresaRepository.deleteById(id);
    }

    @Override
    public Page<EmpresaResponse> findEmpresasByFilters(String search, String ruc, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Specification<Empresa> spec = EmpresaSpecification.filterEmpresas(search, ruc, fechaCreacionDesde, fechaCreacionHasta);
        return empresaRepository.findAll(spec, pageable).map(empresaMapper::toEmpresaResponse);
    }
}
