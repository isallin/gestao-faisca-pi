package com.faisca.backend.projeto.controller;

import com.faisca.backend.projeto.dto.ProjetoCadastroRequest;
import com.faisca.backend.projeto.dto.ProjetoResponse;
import com.faisca.backend.projeto.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {
    private final ProjetoService service;
    public ProjetoController(ProjetoService service) { this.service = service; }

    @GetMapping
    public List<ProjetoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ProjetoResponse buscar(@PathVariable Integer id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<ProjetoResponse> cadastrar(@RequestBody @Valid ProjetoCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ProjetoResponse atualizar(@PathVariable Integer id, @RequestBody @Valid ProjetoCadastroRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
