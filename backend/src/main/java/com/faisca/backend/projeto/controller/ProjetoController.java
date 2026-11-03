package com.faisca.backend.projeto.controller;

import com.faisca.backend.projeto.dto.ProjetoCadastroRequest;
import com.faisca.backend.projeto.dto.ProjetoResponse;
import com.faisca.backend.projeto.service.ProjetoService;
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
@RequestMapping("/api/projetos")
@Tag(name = "Projetos", description = "Projetos vinculados a clientes e fases")
@SecurityRequirement(name = "bearerAuth")
public class ProjetoController {
    private final ProjetoService service;
    public ProjetoController(ProjetoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Retorna todos os projetos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de projetos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjetoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<ProjetoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto", description = "Consulta projeto pelo ID.")
    @ApiResponse(responseCode = "200", description = "Projeto encontrado", content = @Content(schema = @Schema(implementation = ProjetoResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    public ProjetoResponse buscar(@PathVariable Integer id) { return service.buscar(id); }

    @PostMapping
    @Operation(summary = "Cadastrar projeto", description = "Cria projeto associado a um cliente e, opcionalmente, a uma fase.")
    @ApiResponse(responseCode = "201", description = "Projeto criado", content = @Content(schema = @Schema(implementation = ProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou fim anterior ao início")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente ou fase não encontrado")
    public ResponseEntity<ProjetoResponse> cadastrar(@RequestBody @Valid ProjetoCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto", description = "Substitui os dados do projeto existente.")
    @ApiResponse(responseCode = "200", description = "Projeto atualizado", content = @Content(schema = @Schema(implementation = ProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou fim anterior ao início")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto, cliente ou fase não encontrado")
    public ProjetoResponse atualizar(@PathVariable Integer id, @RequestBody @Valid ProjetoCadastroRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto", description = "Remove projeto sem tarefas associadas.")
    @ApiResponse(responseCode = "204", description = "Projeto removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    @ApiResponse(responseCode = "409", description = "Projeto possui tarefas")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
