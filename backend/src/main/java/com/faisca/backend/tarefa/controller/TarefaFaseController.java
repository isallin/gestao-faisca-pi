package com.faisca.backend.tarefa.controller;

import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.dto.TarefaFaseRequest;
import com.faisca.backend.tarefa.dto.TarefaFaseResponse;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.entity.TarefaFase;
import com.faisca.backend.tarefa.entity.TarefaFaseId;
import com.faisca.backend.tarefa.repository.TarefaFaseRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas-fases")
@Tag(name = "Tarefas e Fases", description = "Associação entre projetos, fases e tarefas (tabela tarefa_fase V8)")
@SecurityRequirement(name = "bearerAuth")
public class TarefaFaseController {

    private final TarefaFaseRepository repository;
    private final ProjetoRepository projetos;
    private final FaseRepository fases;
    private final TarefaRepository tarefas;

    public TarefaFaseController(TarefaFaseRepository repository,
                                ProjetoRepository projetos,
                                FaseRepository fases,
                                TarefaRepository tarefas) {
        this.repository = repository;
        this.projetos = projetos;
        this.fases = fases;
        this.tarefas = tarefas;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Listar associações tarefa-fase", description = "Lista vínculos da tabela tarefa_fase com filtros opcionais por projeto, fase ou tarefa.")
    @ApiResponse(responseCode = "200", description = "Lista de associações", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarefaFaseResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<TarefaFaseResponse> listar(@RequestParam(required = false) Integer projetoId,
                                           @RequestParam(required = false) Integer faseId,
                                           @RequestParam(required = false) Integer tarefaId) {
        List<TarefaFase> itens;
        if (projetoId != null) {
            itens = repository.findByIdIdProjeto(projetoId);
        } else if (faseId != null) {
            itens = repository.findByIdIdFase(faseId);
        } else if (tarefaId != null) {
            itens = repository.findByIdPkTarefa(tarefaId);
        } else {
            itens = repository.findAll();
        }
        return itens.stream().map(TarefaFaseResponse::new).toList();
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Associar tarefa a fase e projeto", description = "Cria um vínculo na tabela tarefa_fase entre projeto, fase e tarefa.")
    @ApiResponse(responseCode = "201", description = "Associação criada", content = @Content(schema = @Schema(implementation = TarefaFaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto, fase ou tarefa não encontrado")
    public ResponseEntity<TarefaFaseResponse> criar(@Valid @RequestBody TarefaFaseRequest request) {
        Projeto projeto = projetos.findById(request.getProjetoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        Fase fase = fases.findById(request.getFaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase não encontrada"));
        Tarefa tarefa = tarefas.findById(request.getTarefaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
        TarefaFaseId id = new TarefaFaseId(projeto.getId(), fase.getId(), tarefa.getId());
        TarefaFase entity = repository.findById(id).orElseGet(() -> {
            TarefaFase tf = new TarefaFase();
            tf.setProjeto(projeto);
            tf.setFase(fase);
            tf.setTarefa(tarefa);
            return repository.save(tf);
        });
        return ResponseEntity.status(HttpStatus.CREATED).body(new TarefaFaseResponse(entity));
    }

    @DeleteMapping("/{projetoId}/{faseId}/{tarefaId}")
    @Transactional
    @Operation(summary = "Remover associação tarefa-fase", description = "Remove o vínculo composto entre projeto, fase e tarefa.")
    @ApiResponse(responseCode = "204", description = "Associação removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Associação não encontrada")
    public ResponseEntity<Void> excluir(@PathVariable Integer projetoId,
                                        @PathVariable Integer faseId,
                                        @PathVariable Integer tarefaId) {
        TarefaFaseId id = new TarefaFaseId(projetoId, faseId, tarefaId);
        TarefaFase entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associação tarefa-fase não encontrada"));
        repository.delete(entity);
        return ResponseEntity.noContent().build();
    }
}
