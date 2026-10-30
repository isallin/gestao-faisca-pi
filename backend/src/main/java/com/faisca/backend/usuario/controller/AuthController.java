package com.faisca.backend.usuario.controller;

import com.faisca.backend.usuario.dto.LoginRequest;
import com.faisca.backend.usuario.dto.LoginResponse;
import com.faisca.backend.usuario.dto.UsuarioResponse;
import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import com.faisca.backend.usuario.security.JwtUtil;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository repository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UsuarioRepository repository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        Optional<Usuario> teste = repository.findByEmail(request.getEmail().trim().toLowerCase());

        if (teste.isEmpty() || !encoder.matches(request.getSenha(), teste.get().getSenha())) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = teste.get();

        LoginResponse response = new LoginResponse(
                jwtUtil.gerarToken(usuario),
                "Bearer",
                jwtUtil.getExpiracaoSegundos(),
                new UsuarioResponse(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getSobrenome(),
                        usuario.getEmail(),
                        usuario.getCargo())
        );

        return ResponseEntity.status(200).body(response);
    }
}
