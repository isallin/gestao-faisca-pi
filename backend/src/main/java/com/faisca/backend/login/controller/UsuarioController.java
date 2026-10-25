package com.faisca.backend.login.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UsuarioController {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<Usuario> usuarios = repository.findAll();
        List<UsuarioResponse> res = new ArrayList<>();

        for (int i = 0; i < usuarios.size(); i++) {
            res.add(converterParaResponse(usuarios.get(i)));
        }

        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> listarPorId(@PathVariable Integer id) {
        Optional<Usuario> teste = repository.findById(id);

        if (teste.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(200).body(converterParaResponse(teste.get()));
    }

    // Rota pública (tela de cadastro)
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioCadastroRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (repository.existsByEmail(email)) {
            return ResponseEntity.status(409).build();
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome().trim());
        usuario.setSobrenome(request.getSobrenome().trim());
        usuario.setEmail(email);
        usuario.setCargo(request.getCargo().trim());
        usuario.setSenha(encoder.encode(request.getSenha()));

        Usuario registro = repository.save(usuario);

        return ResponseEntity.status(201).body(converterParaResponse(registro));
    }

    // Edita todos os dados (nome, sobrenome, e-mail e cargo); a senha só muda se for enviada
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Integer id, @RequestBody @Valid UsuarioPutRequest request) {
        Optional<Usuario> teste = repository.findById(id);

        if (teste.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        String email = request.getEmail().trim().toLowerCase();

        if (repository.existsByEmailAndIdNot(email, id)) {
            return ResponseEntity.status(409).build();
        }

        Usuario usuario = teste.get();
        usuario.setNome(request.getNome().trim());
        usuario.setSobrenome(request.getSobrenome().trim());
        usuario.setEmail(email);
        usuario.setCargo(request.getCargo().trim());

        if (request.getSenha() != null) {
            usuario.setSenha(encoder.encode(request.getSenha()));
        }

        Usuario registro = repository.save(usuario);

        return ResponseEntity.status(200).body(converterParaResponse(registro));
    }

    // Edita só os campos enviados
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarParcial(@PathVariable Integer id, @RequestBody @Valid UsuarioPatchRequest request) {
        Optional<Usuario> teste = repository.findById(id);

        if (teste.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Usuario usuario = teste.get();

        if (request.getEmail() != null) {
            String email = request.getEmail().trim().toLowerCase();

            if (repository.existsByEmailAndIdNot(email, id)) {
                return ResponseEntity.status(409).build();
            }
            usuario.setEmail(email);
        }
        if (request.getNome() != null) {
            usuario.setNome(request.getNome().trim());
        }
        if (request.getSobrenome() != null) {
            usuario.setSobrenome(request.getSobrenome().trim());
        }
        if (request.getCargo() != null) {
            usuario.setCargo(request.getCargo().trim());
        }
        if (request.getSenha() != null) {
            usuario.setSenha(encoder.encode(request.getSenha()));
        }

        Usuario registro = repository.save(usuario);

        return ResponseEntity.status(200).body(converterParaResponse(registro));
    }

    // A senha nunca é devolvida
    private UsuarioResponse converterParaResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getSobrenome(), u.getEmail(), u.getCargo());
    }
}
