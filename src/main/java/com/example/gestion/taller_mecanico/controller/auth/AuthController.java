package com.example.gestion.taller_mecanico.controller.auth;

import com.example.gestion.taller_mecanico.model.dto.auth.AuthResponse;
import com.example.gestion.taller_mecanico.model.dto.auth.LoginUsuarioRequest;
import com.example.gestion.taller_mecanico.model.dto.auth.RegisterRequest;
import com.example.gestion.taller_mecanico.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginUsuarioRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
