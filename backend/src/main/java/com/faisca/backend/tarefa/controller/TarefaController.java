package com.faisca.backend.tarefa.controller;

import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.TarefaCategoria;
import com.faisca.backend.tarefa.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {
    private final TarefaService service;
    public TarefaController(TarefaService service) { this.service = service; }
    @GetMapping
    public List<TarefaResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public TarefaResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<TarefaResponse> cadastrar(@RequestBody @Valid TarefaCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }
    @PutMapping("/{id}")
    public TarefaResponse atualizar(@PathVariable Integer id, @RequestBody @Valid TarefaCadastroRequest request) {
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}/status")
    public TarefaResponse atualizarStatus(@PathVariable Integer id, @RequestBody @Valid TarefaStatusRequest request) {
        return service.atualizarStatus(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/categorias")
    public List<TarefaCategoria> listarCategorias() { return service.listarCategorias(); }
    @GetMapping("/fases")
    public List<Fase> listarFases() { return service.listarFases(); }
}
