package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.exceptions.UsuarioNotFoundException;
import com.example.gestion.taller_mecanico.model.dto.auth.AuthResponse;
import com.example.gestion.taller_mecanico.model.dto.auth.LoginUsuarioRequest;
import com.example.gestion.taller_mecanico.model.dto.auth.RegisterRequest;
import com.example.gestion.taller_mecanico.model.entity.Usuario;
import com.example.gestion.taller_mecanico.model.entity.Cliente;
import com.example.gestion.taller_mecanico.repository.ClienteRepository;
import com.example.gestion.taller_mecanico.repository.UsuarioRepository;
import com.example.gestion.taller_mecanico.utils.enums.Rol;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginUsuarioRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        String token = jwtService.getToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .rol(usuario.getRol().name())
                .username(usuario.getUsername())
                .build();
    }

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        Usuario nuevoUsuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .dni(request.getDni())
                .correo(request.getCorreo())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getContrasena()))
                .rol(Rol.CLIENTE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        if (usuarioGuardado.getRol() == Rol.CLIENTE) {
            Cliente nuevoCliente = Cliente.builder()
                    .usuario(usuarioGuardado)
                    .telefono(request.getTelefono())
                    .direccion(request.getDireccion())
                    .build();
            clienteRepository.save(nuevoCliente);
        }

        String jwt = jwtService.getToken(usuarioGuardado);

        return AuthResponse.builder()
                .token(jwt)
                .rol(usuarioGuardado.getRol().name())
                .username(usuarioGuardado.getUsername())
                .build();
    }
}