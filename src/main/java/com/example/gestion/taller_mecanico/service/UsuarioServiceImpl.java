package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.UsuarioNotFoundException;
import com.example.gestion.taller_mecanico.mapper.UsuarioMapper;
import com.example.gestion.taller_mecanico.model.dto.UsuarioClienteRequest;
import com.example.gestion.taller_mecanico.model.dto.UsuarioRequest;
import com.example.gestion.taller_mecanico.model.dto.UsuarioResponse;
import com.example.gestion.taller_mecanico.model.dto.UsuarioTrabajadorRequest;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Trabajador;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.TrabajadorRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.UsuarioRepository;
import com.example.gestion.taller_mecanico.specification.UsuarioSpecification;
import com.example.gestion.taller_mecanico.utils.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final TallerRepository tallerRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse findById(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toUsuarioResponse)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UsuarioResponse save(UsuarioRequest usuarioRequest) {
        Usuario usuario = Usuario.builder()
                .nombreCompleto(usuarioRequest.getNombreCompleto())
                .dni(usuarioRequest.getDni())
                .correo(usuarioRequest.getCorreo())
                .username(usuarioRequest.getUsername())
                .password(passwordEncoder.encode(usuarioRequest.getContrasena()))
                .rol(Rol.valueOf(usuarioRequest.getRol().toUpperCase()))
                .build();
        return usuarioMapper.toUsuarioResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse update(Long id, UsuarioRequest usuarioRequest) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombreCompleto(usuarioRequest.getNombreCompleto());
                    usuario.setDni(usuarioRequest.getDni());
                    usuario.setCorreo(usuarioRequest.getCorreo());
                    usuario.setUsername(usuarioRequest.getUsername());
                    if (usuarioRequest.getContrasena() != null && !usuarioRequest.getContrasena().trim().isEmpty()) {
                        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getContrasena()));
                    }
                    usuario.setRol(Rol.valueOf(usuarioRequest.getRol().toUpperCase()));
                    return usuarioMapper.toUsuarioResponse(usuarioRepository.save(usuario));
                }).orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UsuarioResponse createUsuarioCliente(UsuarioClienteRequest request) {
        Usuario nuevoUsuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .dni(request.getDni())
                .correo(request.getCorreo())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getContrasena()))
                .rol(Rol.CLIENTE)
                .build();
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Cliente nuevoCliente = Cliente.builder()
                .usuario(usuarioGuardado)
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .tallerAsignado(request.getTallerAsignadoId() != null
                        ? tallerRepository.findById(request.getTallerAsignadoId()).orElse(null)
                        : null)
                .build();
        clienteRepository.save(nuevoCliente);

        return usuarioMapper.toUsuarioResponse(usuarioGuardado);
    }

    @Override
    public UsuarioResponse createUsuarioTrabajador(UsuarioTrabajadorRequest request) {
        Usuario nuevoUsuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .dni(request.getDni())
                .correo(request.getCorreo())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getContrasena()))
                .rol(Rol.TRABAJADOR)
                .build();
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Taller taller = tallerRepository.findById(request.getTallerId())
                .orElseThrow(
                        () -> new TallerNotFoundException("Taller no encontrado con ID: " + request.getTallerId()));

        Trabajador nuevoTrabajador = Trabajador.builder()
                .usuario(usuarioGuardado)
                .especialidad(request.getEspecialidad())
                .taller(taller)
                .build();
        trabajadorRepository.save(nuevoTrabajador);

        return usuarioMapper.toUsuarioResponse(usuarioGuardado);
    }

    @Override
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Page<UsuarioResponse> findUsuariosByFilters(String search, String rol, LocalDateTime fechaCreacionDesde,
            LocalDateTime fechaCreacionHasta, LocalDateTime fechaActualizacionDesde,
            LocalDateTime fechaActualizacionHasta, Pageable pageable) {
        Specification<Usuario> spec = UsuarioSpecification.filterUsuarios(search, rol, fechaCreacionDesde,
                fechaCreacionHasta, fechaActualizacionDesde, fechaActualizacionHasta);
        return usuarioRepository.findAll(spec, pageable).map(usuarioMapper::toUsuarioResponse);
    }
}