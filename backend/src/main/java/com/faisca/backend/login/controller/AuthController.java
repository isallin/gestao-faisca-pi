package com.faisca.backend.login.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
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

        // mesma resposta para e-mail inexistente e senha errada (não revela quais e-mails existem)
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
