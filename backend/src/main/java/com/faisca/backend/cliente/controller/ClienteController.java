package com.faisca.backend.cliente.controller;

import com.faisca.backend.cliente.dto.*;
import com.faisca.backend.cliente.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping
    public List<ClienteResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }
    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Integer id, @RequestBody @Valid ClienteCadastroRequest request) {
        return service.atualizar(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
