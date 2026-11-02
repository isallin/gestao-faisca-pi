package com.faisca.backend.usuario.controller;

import com.faisca.backend.usuario.dto.*;
import com.faisca.backend.usuario.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }
    @GetMapping
    public List<UsuarioResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Integer id, @RequestBody @Valid UsuarioPutRequest request) {
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}")
    public UsuarioResponse atualizarParcial(@PathVariable Integer id, @RequestBody @Valid UsuarioPatchRequest request) {
        return service.atualizarParcial(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id, HttpServletRequest request) {
        service.excluir(id, (Integer) request.getAttribute("usuarioId"));
        return ResponseEntity.noContent().build();
    }
}
