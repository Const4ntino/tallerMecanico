package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.auth.AuthResponse;
import com.example.gestion.taller_mecanico.model.dto.auth.LoginUsuarioRequest;
import com.example.gestion.taller_mecanico.model.dto.auth.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    AuthResponse login(LoginUsuarioRequest request);
    AuthResponse register(RegisterRequest request);
}