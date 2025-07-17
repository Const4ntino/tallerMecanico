package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.ClienteNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.TallerNotFoundException;
import com.example.gestion.taller_mecanico.exceptions.UsuarioNotFoundException;
import com.example.gestion.taller_mecanico.mapper.ClienteMapper;
import com.example.gestion.taller_mecanico.model.dto.ClienteRequest;
import com.example.gestion.taller_mecanico.model.dto.ClienteResponse;
import com.example.gestion.taller_mecanico.model.dto.UsuarioClienteRequest;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.model.entity.Taller;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.TallerRepository;
import com.example.gestion.taller_mecanico.repository.UsuarioRepository;
import com.example.gestion.taller_mecanico.specification.ClienteSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TallerRepository tallerRepository;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toClienteResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponse findById(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toClienteResponse)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public ClienteResponse findByUsuarioId(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .map(clienteMapper::toClienteResponse)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario con ID: " + usuarioId));
    }

    @Override
    public ClienteResponse save(ClienteRequest clienteRequest) {
        Usuario usuario = usuarioRepository.findById(clienteRequest.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + clienteRequest.getUsuarioId()));

        Taller tallerAsignado = null;
        if (clienteRequest.getTallerAsignadoId() != null) {
            tallerAsignado = tallerRepository.findById(clienteRequest.getTallerAsignadoId())
                    .orElseThrow(() -> new TallerNotFoundException("Taller asignado no encontrado con ID: " + clienteRequest.getTallerAsignadoId()));
        }

        Cliente cliente = Cliente.builder()
                .usuario(usuario)
                .telefono(clienteRequest.getTelefono())
                .direccion(clienteRequest.getDireccion())
                .tallerAsignado(tallerAsignado)
                .build();
        return clienteMapper.toClienteResponse(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponse update(Long id, ClienteRequest clienteRequest) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    Usuario usuario = usuarioRepository.findById(clienteRequest.getUsuarioId())
                            .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + clienteRequest.getUsuarioId()));

                    Taller tallerAsignado = null;
                    if (clienteRequest.getTallerAsignadoId() != null) {
                        tallerAsignado = tallerRepository.findById(clienteRequest.getTallerAsignadoId())
                                .orElseThrow(() -> new TallerNotFoundException("Taller asignado no encontrado con ID: " + clienteRequest.getTallerAsignadoId()));
                    }

                    cliente.setUsuario(usuario);
                    cliente.setTelefono(clienteRequest.getTelefono());
                    cliente.setDireccion(clienteRequest.getDireccion());
                    cliente.setTallerAsignado(tallerAsignado);
                    return clienteMapper.toClienteResponse(clienteRepository.save(cliente));
                }).orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    public ClienteResponse getMisDatos() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));
        return clienteMapper.toClienteResponse(cliente);
    }

    @Transactional
    @Override
    public ClienteResponse updateMisDatos(UsuarioClienteRequest usuarioClienteRequest) {
        try {
            Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado para el usuario autenticado."));

            usuario.setNombreCompleto(usuarioClienteRequest.getNombreCompleto());
            usuario.setCorreo(usuarioClienteRequest.getCorreo());
            usuario.setUsername(usuarioClienteRequest.getUsername());

            if (usuarioClienteRequest.getContrasena() != null &&
                    !usuarioClienteRequest.getContrasena().trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioClienteRequest.getContrasena()));
            }

            cliente.setTelefono(usuarioClienteRequest.getTelefono());
            cliente.setDireccion(usuarioClienteRequest.getDireccion());

            usuarioRepository.save(usuario);
            clienteRepository.save(cliente);

            return clienteMapper.toClienteResponse(cliente);
        } catch (Exception ex) {
            throw new RuntimeException("Error al actualizar los datos del cliente", ex);
        }
    }

    @Override
    public Page<ClienteResponse> findClientesByFilters(String search, Long tallerAsignadoId, String telefono, LocalDateTime fechaCreacionDesde, LocalDateTime fechaCreacionHasta, Pageable pageable) {
        Specification<Cliente> spec = ClienteSpecification.filterClientes(search, tallerAsignadoId, telefono, fechaCreacionDesde, fechaCreacionHasta);
        return clienteRepository.findAll(spec, pageable).map(clienteMapper::toClienteResponse);
    }
}