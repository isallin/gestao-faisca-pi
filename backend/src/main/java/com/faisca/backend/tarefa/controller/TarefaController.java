package com.faisca.backend.tarefa.controller;

import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.TarefaCategoria;
import com.faisca.backend.tarefa.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@Tag(name = "Tarefas", description = "Tarefas, categorias, fases e checklist")
@SecurityRequirement(name = "bearerAuth")
public class TarefaController {
    private final TarefaService service;
    public TarefaController(TarefaService service) { this.service = service; }
    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Retorna todas as tarefas com seus checklists.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarefaResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<TarefaResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa", description = "Consulta tarefa pelo ID.")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public TarefaResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping
    @Operation(summary = "Cadastrar tarefa", description = "Cria tarefa vinculada a projeto, categoria e fase.")
    @ApiResponse(responseCode = "201", description = "Tarefa criada", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou datas incoerentes")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto, categoria, fase ou colaborador não encontrado")
    public ResponseEntity<TarefaResponse> cadastrar(@RequestBody @Valid TarefaCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Substitui dados, checklist e status de uma tarefa.")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou datas incoerentes")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou vínculo não encontrado")
    public TarefaResponse atualizar(@PathVariable Integer id, @RequestBody @Valid TarefaCadastroRequest request) {
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da tarefa", description = "Adiciona um novo status quando diferente do atual.")
    @ApiResponse(responseCode = "200", description = "Status atualizado", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Status inválido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public TarefaResponse atualizarStatus(@PathVariable Integer id, @RequestBody @Valid TarefaStatusRequest request) {
        return service.atualizarStatus(id, request);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tarefa", description = "Remove uma tarefa pelo ID.")
    @ApiResponse(responseCode = "204", description = "Tarefa removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias de tarefas", description = "Retorna os IDs e nomes das categorias.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarefaCategoria.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<TarefaCategoria> listarCategorias() { return service.listarCategorias(); }
    @GetMapping("/fases")
    @Operation(summary = "Listar fases para tarefas", description = "Retorna as fases disponíveis para vincular tarefas.")
    @ApiResponse(responseCode = "200", description = "Lista de fases", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fase.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<Fase> listarFases() { return service.listarFases(); }
}
