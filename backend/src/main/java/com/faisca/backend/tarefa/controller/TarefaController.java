package com.faisca.backend.tarefa.controller;

import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.dto.TarefaCadastroRequest;
import com.faisca.backend.tarefa.entity.StatusTarefa;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.entity.TarefaCategoria;
import com.faisca.backend.tarefa.repository.TarefaCategoriaRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import com.faisca.backend.tarefa.dto.TarefaResponse;
import com.faisca.backend.tarefa.dto.TarefaStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final TarefaCategoriaRepository categoriaRepository;
    private final FaseRepository faseRepository;

    public TarefaController(TarefaRepository tarefaRepository,
                            ProjetoRepository projetoRepository,
                            TarefaCategoriaRepository categoriaRepository,
                            FaseRepository faseRepository) {
        this.tarefaRepository = tarefaRepository;
        this.projetoRepository = projetoRepository;
        this.categoriaRepository = categoriaRepository;
        this.faseRepository = faseRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> cadastrar(@RequestBody @Valid TarefaCadastroRequest request) {
        if (request.getDataFimPrev().isBefore(request.getDataInicioPrev())) {
            return ResponseEntity.status(400).body("Fim previsto não pode ser anterior ao início previsto");
        }

        if (request.getDataInicioReal() != null && request.getDataFimReal() != null
                && request.getDataFimReal().isBefore(request.getDataInicioReal())) {
            return ResponseEntity.status(400).body("Fim real não pode ser anterior ao início real");
        }

        Projeto projeto = projetoRepository.findById(request.getProjetoId()).orElse(null);
        if (projeto == null) {
            return ResponseEntity.status(404).body("Projeto não encontrado");
        }

        TarefaCategoria categoria = categoriaRepository.findById(request.getCategoriaId()).orElse(null);
        if (categoria == null) {
            return ResponseEntity.status(404).body("Categoria não encontrada");
        }

        if (!faseRepository.existsById(request.getFkTarefaFase())) {
            return ResponseEntity.status(404).body("Fase não encontrada");
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setProjeto(projeto);
        tarefa.setCategoria(categoria);
        tarefa.setFkTarefaFase(request.getFkTarefaFase());
        tarefa.setNome(request.getNome().trim());
        tarefa.setPrioridade(request.getPrioridade());
        tarefa.setDataInicioPrev(request.getDataInicioPrev());
        tarefa.setDataFimPrev(request.getDataFimPrev());
        tarefa.setDataInicioReal(request.getDataInicioReal());
        tarefa.setDataFimReal(request.getDataFimReal());
        tarefa.setObservacao(tratarTexto(request.getObservacao()));

        StatusTarefa status = new StatusTarefa();
        status.setStatus(request.getStatus().trim());
        tarefa.adicionarStatus(status);

        Tarefa registro = tarefaRepository.save(tarefa);
        return ResponseEntity.status(201).body(new TarefaResponse(registro));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<TarefaResponse>> listar() {
        List<TarefaResponse> resposta = new ArrayList<>();

        for (Tarefa tarefa : tarefaRepository.findAll()) {
            resposta.add(new TarefaResponse(tarefa));
        }

        return ResponseEntity.ok(resposta);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<TarefaResponse> atualizarStatus(@PathVariable Integer id,
            @RequestBody @Valid TarefaStatusRequest request) {
        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa == null) {
            return ResponseEntity.notFound().build();
        }
        if (!request.getStatus().equals(tarefa.getStatusAtual())) {
            StatusTarefa status = new StatusTarefa();
            status.setStatus(request.getStatus());
            tarefa.adicionarStatus(status);
            tarefaRepository.saveAndFlush(tarefa);
        }
        return ResponseEntity.ok(new TarefaResponse(tarefa));
    }

    @GetMapping("/categorias")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TarefaCategoria>> listarCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @GetMapping("/fases")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Fase>> listarFases() {
        return ResponseEntity.ok(faseRepository.findAll());
    }

    private String tratarTexto(String texto) {
        return texto == null ? null : texto.trim();
    }
}
