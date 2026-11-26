package com.faisca.backend.tarefa.controller;

import com.faisca.backend.tarefa.dto.PrioridadeRequest;
import com.faisca.backend.tarefa.dto.PrioridadeResponse;
import com.faisca.backend.tarefa.service.PrioridadeService;
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

@RestController
@RequestMapping("/api/prioridades")
@Tag(name = "Prioridades", description = "Cadastro de níveis de prioridade das tarefas (V8)")
@SecurityRequirement(name = "bearerAuth")
public class PrioridadeController {

    private final PrioridadeService service;

    public PrioridadeController(PrioridadeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar prioridades", description = "Retorna todos os níveis de prioridade cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de prioridades", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrioridadeResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<PrioridadeResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar prioridade", description = "Consulta uma prioridade pelo ID.")
    @ApiResponse(responseCode = "200", description = "Prioridade encontrada", content = @Content(schema = @Schema(implementation = PrioridadeResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Prioridade não encontrada")
    public PrioridadeResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar prioridade", description = "Cria um novo nível de prioridade.")
    @ApiResponse(responseCode = "201", description = "Prioridade criada", content = @Content(schema = @Schema(implementation = PrioridadeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "409", description = "Prioridade já cadastrada")
    public ResponseEntity<PrioridadeResponse> criar(@Valid @RequestBody PrioridadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar prioridade", description = "Altera o nome de uma prioridade existente.")
    @ApiResponse(responseCode = "200", description = "Prioridade atualizada", content = @Content(schema = @Schema(implementation = PrioridadeResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Prioridade não encontrada")
    @ApiResponse(responseCode = "409", description = "Prioridade já cadastrada")
    public PrioridadeResponse atualizar(@PathVariable Integer id, @Valid @RequestBody PrioridadeRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir prioridade", description = "Remove uma prioridade sem tarefas associadas.")
    @ApiResponse(responseCode = "204", description = "Prioridade removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Prioridade não encontrada")
    @ApiResponse(responseCode = "409", description = "Prioridade vinculada a tarefas")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
