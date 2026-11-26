package com.faisca.backend.fase.controller;

import com.faisca.backend.fase.dto.FaseRequest;
import com.faisca.backend.fase.dto.FaseResponse;
import com.faisca.backend.fase.service.FaseService;
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
@RequestMapping("/api/fases")
@Tag(name = "Fases", description = "Cadastro e consulta de fases ordenadas (V8)")
@SecurityRequirement(name = "bearerAuth")
public class FaseController {

    private final FaseService service;

    public FaseController(FaseService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar fases", description = "Retorna fases ordenadas pelo campo ordem.")
    @ApiResponse(responseCode = "200", description = "Lista de fases", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FaseResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<FaseResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fase", description = "Consulta uma fase pelo ID.")
    @ApiResponse(responseCode = "200", description = "Fase encontrada", content = @Content(schema = @Schema(implementation = FaseResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fase não encontrada")
    public FaseResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar fase", description = "Cria uma nova fase de projeto/tarefa.")
    @ApiResponse(responseCode = "201", description = "Fase criada", content = @Content(schema = @Schema(implementation = FaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "409", description = "Fase já cadastrada")
    public ResponseEntity<FaseResponse> criar(@Valid @RequestBody FaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fase", description = "Altera nome e ordem de uma fase existente.")
    @ApiResponse(responseCode = "200", description = "Fase atualizada", content = @Content(schema = @Schema(implementation = FaseResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fase não encontrada")
    @ApiResponse(responseCode = "409", description = "Fase já cadastrada")
    public FaseResponse atualizar(@PathVariable Integer id, @Valid @RequestBody FaseRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir fase", description = "Remove uma fase sem projetos ou tarefas associados.")
    @ApiResponse(responseCode = "204", description = "Fase removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fase não encontrada")
    @ApiResponse(responseCode = "409", description = "Fase vinculada a projetos ou tarefas")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
