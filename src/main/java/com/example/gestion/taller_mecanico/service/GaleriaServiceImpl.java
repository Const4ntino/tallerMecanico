package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.ClienteNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.GaleriaNotFoundException;
import com.example.gestion.taller_mecanico.mapper.GaleriaMapper;
import com.example.gestion.taller_mecanico.model.dto.GaleriaRequest;
import com.example.gestion.taller_mecanico.model.dto.GaleriaResponse;
import com.example.gestion.taller_mecanico.model.entity.Galeria;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.repository.GaleriaRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.specification.GaleriaSpecification;
import com.example.gestion.taller_mecanico.utils.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GaleriaServiceImpl implements GaleriaService {

    private final GaleriaRepository galeriaRepository;
    private final TallerRepository tallerRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final ClienteRepository clienteRepository;
    private final GaleriaMapper galeriaMapper;

    @Override
    public List<GaleriaResponse> findAll() {
        return galeriaRepository.findAll().stream()
                .map(galeriaMapper::toGaleriaResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GaleriaResponse findById(Long id) {
        return galeriaRepository.findById(id)
                .map(galeriaMapper::toGaleriaResponse)
                .orElseThrow(() -> new GaleriaNotFoundException("Galería no encontrada con ID: " + id));
    }

    @Override
    public GaleriaResponse save(GaleriaRequest galeriaRequest) {
        Taller taller = tallerRepository.findById(galeriaRequest.getTallerId())
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + galeriaRequest.getTallerId()));

        Galeria galeria = Galeria.builder()
                .taller(taller)
                .imagenUrl(galeriaRequest.getImagenUrl())
                .titulo(galeriaRequest.getTitulo())
                .descripcion(galeriaRequest.getDescripcion())
                .tipo(galeriaRequest.getTipo())
                .build();
        return galeriaMapper.toGaleriaResponse(galeriaRepository.save(galeria));
    }

    @Override
    public GaleriaResponse update(Long id, GaleriaRequest galeriaRequest) {
        return galeriaRepository.findById(id)
                .map(galeria -> {
                    Taller taller = tallerRepository.findById(galeriaRequest.getTallerId())
                            .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + galeriaRequest.getTallerId()));

                    galeria.setTaller(taller);
                    galeria.setImagenUrl(galeriaRequest.getImagenUrl());
                    galeria.setTitulo(galeriaRequest.getTitulo());
                    galeria.setDescripcion(galeriaRequest.getDescripcion());
                    galeria.setTipo(galeriaRequest.getTipo());
                    return galeriaMapper.toGaleriaResponse(galeriaRepository.save(galeria));
                }).orElseThrow(() -> new GaleriaNotFoundException("Galería no encontrada con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!galeriaRepository.existsById(id)) {
            throw new GaleriaNotFoundException("Galería no encontrada con ID: " + id);
        }
        galeriaRepository.deleteById(id);
    }

    @Override
    public Page<GaleriaResponse> findGaleriasByFilters(String search, Long tallerId, String tipo, Pageable pageable) {
        Specification<Galeria> spec = GaleriaSpecification.filterGalerias(search, tallerId, tipo);
        return galeriaRepository.findAll(spec, pageable).map(galeriaMapper::toGaleriaResponse);
    }

    @Override
    public Page<GaleriaResponse> findMyTallerGaleriasByFilters(String search, String tipo, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long tallerId = null;

        if (usuario.getRol() == Rol.ADMINISTRADOR_TALLER || usuario.getRol() == Rol.TRABAJADOR) {
            Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario autenticado."));
            tallerId = trabajador.getTaller().getId();
        } else if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
            if (cliente.getTallerAsignado() != null) {
                tallerId = cliente.getTallerAsignado().getId();
            }
        }

        Specification<Galeria> spec = GaleriaSpecification.filterGalerias(search, tallerId, tipo);
        return galeriaRepository.findAll(spec, pageable).map(galeriaMapper::toGaleriaResponse);
    }
}
