package com.faisca.backend.tarefa.controller;

import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.TarefaCategoria;
import com.faisca.backend.tarefa.service.TarefaService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarefas")
@Tag(name = "Tarefas", description = "Tarefas, categorias, prioridades, fases, subtarefas, colaboradores e checklist (V8)")
@SecurityRequirement(name = "bearerAuth")
public class TarefaController {

    private final TarefaService service;

    public TarefaController(TarefaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Retorna todas as tarefas com prioridades, colaboradores, subtarefas e checklists.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarefaResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<TarefaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa", description = "Consulta tarefa pelo ID.")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public TarefaResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar tarefa", description = "Cria tarefa vinculada a projeto, categoria, prioridade, fase, colaboradores e subtarefas.")
    @ApiResponse(responseCode = "201", description = "Tarefa criada", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou datas incoerentes")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto, categoria, prioridade, fase ou colaborador não encontrado")
    public ResponseEntity<TarefaResponse> cadastrar(@RequestBody @Valid TarefaCadastroRequest request,
                                                    @RequestAttribute("usuarioId") Integer usuarioId) {
        return ResponseEntity.status(201).body(service.criar(request, usuarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Substitui dados, prioridade, colaboradores, subtarefas, checklist e status de uma tarefa.")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou datas incoerentes")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou vínculo não encontrado")
    public TarefaResponse atualizar(@PathVariable Integer id, @RequestBody @Valid TarefaCadastroRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da tarefa", description = "Atualiza o status atual da tarefa.")
    @ApiResponse(responseCode = "200", description = "Status atualizado", content = @Content(schema = @Schema(implementation = TarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Status inválido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public TarefaResponse atualizarStatus(@PathVariable Integer id, @RequestBody @Valid TarefaStatusRequest request) {
        return service.atualizarStatus(id, request);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Listar registros de status da tarefa", description = "Retorna os registros de status vinculados à tarefa.")
    @ApiResponse(responseCode = "200", description = "Registros de status", content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatusTarefaResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public List<StatusTarefaResponse> listarStatus(@PathVariable Integer id) {
        return service.listarStatus(id);
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

    // -------- Categorias --------

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias de tarefas", description = "Retorna os IDs e nomes das categorias.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarefaCategoria.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<TarefaCategoria> listarCategorias() {
        return service.listarCategorias();
    }

    @PostMapping("/categorias")
    @Operation(summary = "Cadastrar categoria de tarefa", description = "Cria uma nova categoria para tarefas.")
    @ApiResponse(responseCode = "201", description = "Categoria criada", content = @Content(schema = @Schema(implementation = TarefaCategoria.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    public ResponseEntity<TarefaCategoria> criarCategoria(@Valid @RequestBody TarefaCategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarCategoria(request));
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Atualizar categoria de tarefa", description = "Atualiza o nome de uma categoria de tarefa.")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada", content = @Content(schema = @Schema(implementation = TarefaCategoria.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    public TarefaCategoria atualizarCategoria(@PathVariable Integer id, @Valid @RequestBody TarefaCategoriaRequest request) {
        return service.atualizarCategoria(id, request);
    }

    @DeleteMapping("/categorias/{id}")
    @Operation(summary = "Excluir categoria de tarefa", description = "Remove uma categoria de tarefa sem vínculos.")
    @ApiResponse(responseCode = "204", description = "Categoria removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Categoria vinculada a tarefas")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Integer id) {
        service.excluirCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fases")
    @Operation(summary = "Listar fases para tarefas", description = "Retorna as fases disponíveis para vincular tarefas.")
    @ApiResponse(responseCode = "200", description = "Lista de fases", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fase.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<Fase> listarFases() {
        return service.listarFases();
    }

    @GetMapping("/prioridades")
    @Operation(summary = "Listar prioridades para tarefas", description = "Retorna as prioridades cadastradas na tabela prioridade (V8).")
    @ApiResponse(responseCode = "200", description = "Lista de prioridades", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrioridadeResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<PrioridadeResponse> listarPrioridades() {
        return service.listarPrioridades();
    }

    // -------- Subtarefas (V8) --------

    @GetMapping("/{tarefaId}/subtarefas")
    @Operation(summary = "Listar subtarefas", description = "Retorna todas as subtarefas de uma tarefa.")
    @ApiResponse(responseCode = "200", description = "Lista de subtarefas", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubtarefaResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public List<SubtarefaResponse> listarSubtarefas(@PathVariable Integer tarefaId) {
        return service.listarSubtarefas(tarefaId);
    }

    @PostMapping("/{tarefaId}/subtarefas")
    @Operation(summary = "Criar subtarefa", description = "Adiciona uma nova subtarefa à tarefa.")
    @ApiResponse(responseCode = "201", description = "Subtarefa criada", content = @Content(schema = @Schema(implementation = SubtarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<SubtarefaResponse> criarSubtarefa(@PathVariable Integer tarefaId,
                                                            @Valid @RequestBody SubtarefaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarSubtarefa(tarefaId, request));
    }

    @PutMapping("/{tarefaId}/subtarefas/{subtarefaId}")
    @Operation(summary = "Atualizar subtarefa", description = "Atualiza nome e marcação de conclusão de uma subtarefa.")
    @ApiResponse(responseCode = "200", description = "Subtarefa atualizada", content = @Content(schema = @Schema(implementation = SubtarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou subtarefa não encontrada")
    public SubtarefaResponse atualizarSubtarefa(@PathVariable Integer tarefaId,
                                                @PathVariable Integer subtarefaId,
                                                @Valid @RequestBody SubtarefaRequest request) {
        return service.atualizarSubtarefa(tarefaId, subtarefaId, request);
    }

    @PatchMapping("/{tarefaId}/subtarefas/{subtarefaId}/check")
    @Operation(summary = "Alternar conclusão da subtarefa", description = "Marca, desmarca ou alterna o campo check da subtarefa.")
    @ApiResponse(responseCode = "200", description = "Subtarefa atualizada", content = @Content(schema = @Schema(implementation = SubtarefaResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou subtarefa não encontrada")
    public SubtarefaResponse alternarCheckSubtarefa(@PathVariable Integer tarefaId,
                                                    @PathVariable Integer subtarefaId,
                                                    @RequestBody(required = false) Map<String, Boolean> body) {
        Boolean check = body == null ? null : body.get("check");
        return service.alternarCheckSubtarefa(tarefaId, subtarefaId, check);
    }

    @DeleteMapping("/{tarefaId}/subtarefas/{subtarefaId}")
    @Operation(summary = "Excluir subtarefa", description = "Remove uma subtarefa da tarefa.")
    @ApiResponse(responseCode = "204", description = "Subtarefa removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou subtarefa não encontrada")
    public ResponseEntity<Void> excluirSubtarefa(@PathVariable Integer tarefaId, @PathVariable Integer subtarefaId) {
        service.excluirSubtarefa(tarefaId, subtarefaId);
        return ResponseEntity.noContent().build();
    }

    // -------- Colaboradores da tarefa (colaborador_tarefa V8) --------

    @GetMapping("/{tarefaId}/colaboradores")
    @Operation(summary = "Listar colaboradores da tarefa", description = "Retorna os colaboradores atribuídos à tarefa com data de atribuição.")
    @ApiResponse(responseCode = "200", description = "Lista de colaboradores atribuídos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ColaboradorTarefaResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public List<ColaboradorTarefaResponse> listarColaboradores(@PathVariable Integer tarefaId) {
        return service.listarColaboradores(tarefaId);
    }

    @PostMapping("/{tarefaId}/colaboradores")
    @Operation(summary = "Atribuir colaborador à tarefa", description = "Associa um colaborador à tarefa na tabela colaborador_tarefa com data de atribuição.")
    @ApiResponse(responseCode = "201", description = "Colaborador atribuído", content = @Content(schema = @Schema(implementation = ColaboradorTarefaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou colaborador não encontrado")
    public ResponseEntity<ColaboradorTarefaResponse> atribuirColaborador(@PathVariable Integer tarefaId,
                                                                         @Valid @RequestBody ColaboradorTarefaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.atribuirColaborador(tarefaId, request));
    }

    @DeleteMapping("/{tarefaId}/colaboradores/{colaboradorId}")
    @Operation(summary = "Desvincular colaborador da tarefa", description = "Remove o colaborador da tarefa caso não haja registros de horas vinculados.")
    @ApiResponse(responseCode = "204", description = "Colaborador desvinculado")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Atribuição não encontrada")
    @ApiResponse(responseCode = "409", description = "Colaborador possui registros de horas nesta tarefa")
    public ResponseEntity<Void> removerColaborador(@PathVariable Integer tarefaId, @PathVariable Integer colaboradorId) {
        service.removerColaborador(tarefaId, colaboradorId);
        return ResponseEntity.noContent().build();
    }
}
