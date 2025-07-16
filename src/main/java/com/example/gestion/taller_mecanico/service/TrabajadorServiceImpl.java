package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TrabajadorNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.UsuarioNotFoundException;
import com.example.gestion.taller_mecanico.mapper.TrabajadorMapper;
import com.example.gestion.taller_mecanico.model.dto.TrabajadorRequest;
import com.example.gestion.taller_mecanico.model.dto.TrabajadorResponse;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.repository.UsuarioRepository;
import com.example.gestion.taller_mecanico.specification.TrabajadorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // Importar Specification
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final TallerRepository tallerRepository;
    private final TrabajadorMapper trabajadorMapper;

    @Override
    public List<TrabajadorResponse> findAll() {
        return trabajadorRepository.findAll().stream()
                .map(trabajadorMapper::toTrabajadorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TrabajadorResponse findById(Long id) {
        return trabajadorRepository.findById(id)
                .map(trabajadorMapper::toTrabajadorResponse)
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado con ID: " + id));
    }

    @Override
    public TrabajadorResponse findByUsuarioId(Long usuarioId) {
        return trabajadorRepository.findByUsuarioId(usuarioId)
                .map(trabajadorMapper::toTrabajadorResponse)
                .orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado para el usuario con ID: " + usuarioId));
    }

    @Override
    public TrabajadorResponse save(TrabajadorRequest trabajadorRequest) {
        Usuario usuario = usuarioRepository.findById(trabajadorRequest.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + trabajadorRequest.getUsuarioId()));

        Taller taller = tallerRepository.findById(trabajadorRequest.getTallerId())
                .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + trabajadorRequest.getTallerId()));

        Trabajador trabajador = Trabajador.builder()
                .usuario(usuario)
                .especialidad(trabajadorRequest.getEspecialidad())
                .taller(taller)
                .build();
        return trabajadorMapper.toTrabajadorResponse(trabajadorRepository.save(trabajador));
    }

    @Override
    public TrabajadorResponse update(Long id, TrabajadorRequest trabajadorRequest) {
        return trabajadorRepository.findById(id)
                .map(trabajador -> {
                    Usuario usuario = usuarioRepository.findById(trabajadorRequest.getUsuarioId())
                            .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + trabajadorRequest.getUsuarioId()));

                    Taller taller = tallerRepository.findById(trabajadorRequest.getTallerId())
                            .orElseThrow(() -> new TallerNotFoundException("Taller no encontrado con ID: " + trabajadorRequest.getTallerId()));

                    trabajador.setUsuario(usuario);
                    trabajador.setEspecialidad(trabajadorRequest.getEspecialidad());
                    trabajador.setTaller(taller);
                    return trabajadorMapper.toTrabajadorResponse(trabajadorRepository.save(trabajador));
                }).orElseThrow(() -> new TrabajadorNotFoundException("Trabajador no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!trabajadorRepository.existsById(id)) {
            throw new TrabajadorNotFoundException("Trabajador no encontrado con ID: " + id);
        }
        trabajadorRepository.deleteById(id);
    }

    @Override
    public Page<TrabajadorResponse> findTrabajadoresByFilters(String search, String especialidad, Long tallerId, String rol, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Specification<Trabajador> spec = TrabajadorSpecification.filterTrabajadores(search, especialidad, tallerId, rol, fechaCreacionDesde, fechaCreacionHasta);
        return trabajadorRepository.findAll(spec, pageable).map(trabajadorMapper::toTrabajadorResponse);
    }
}
