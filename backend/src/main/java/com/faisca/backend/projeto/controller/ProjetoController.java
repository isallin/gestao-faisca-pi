package com.faisca.backend.projeto.controller;

import com.faisca.backend.fornecedor.dto.FornecedorProjetoResponse;
import com.faisca.backend.projeto.dto.*;
import com.faisca.backend.projeto.service.ProjetoService;
import com.faisca.backend.tarefa.dto.TarefaFaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@Tag(name = "Projetos", description = "Projetos vinculados a clientes, endereços, fases e fornecedores (V8)")
@SecurityRequirement(name = "bearerAuth")
public class ProjetoController {

    private final ProjetoService service;

    public ProjetoController(ProjetoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Retorna todos os projetos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de projetos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjetoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<ProjetoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto", description = "Consulta projeto pelo ID com endereço e fornecedores vinculados.")
    @ApiResponse(responseCode = "200", description = "Projeto encontrado", content = @Content(schema = @Schema(implementation = ProjetoResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    public ProjetoResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar projeto", description = "Cria projeto associado a um cliente, endereço e fase.")
    @ApiResponse(responseCode = "201", description = "Projeto criado", content = @Content(schema = @Schema(implementation = ProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou fim anterior ao início")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente ou fase não encontrado")
    public ResponseEntity<ProjetoResponse> cadastrar(@RequestBody @Valid ProjetoCadastroRequest request,
                                                     @RequestAttribute("usuarioId") Integer usuarioId) {
        return ResponseEntity.status(201).body(service.criar(request, usuarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto", description = "Substitui os dados e endereço do projeto existente.")
    @ApiResponse(responseCode = "200", description = "Projeto atualizado", content = @Content(schema = @Schema(implementation = ProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou fim anterior ao início")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto, cliente ou fase não encontrado")
    public ProjetoResponse atualizar(@PathVariable Integer id, @RequestBody @Valid ProjetoCadastroRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto", description = "Remove projeto sem tarefas ou fornecedores associados.")
    @ApiResponse(responseCode = "204", description = "Projeto removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    @ApiResponse(responseCode = "409", description = "Projeto possui tarefas ou fornecedores vinculados")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // -------- Endereço dedicado (endereco_projeto V8) --------

    @GetMapping("/{id}/endereco")
    @Operation(summary = "Buscar endereço do projeto", description = "Retorna o endereço dedicado do projeto (tabela endereco_projeto).")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado", content = @Content(schema = @Schema(implementation = EnderecoProjetoResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto ou endereço não encontrado")
    public EnderecoProjetoResponse buscarEndereco(@PathVariable Integer id) {
        return service.buscarEndereco(id);
    }

    @PutMapping("/{id}/endereco")
    @Operation(summary = "Salvar endereço do projeto", description = "Cria ou atualiza o endereço dedicado do projeto (1:1).")
    @ApiResponse(responseCode = "200", description = "Endereço salvo", content = @Content(schema = @Schema(implementation = EnderecoProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    public EnderecoProjetoResponse salvarEndereco(@PathVariable Integer id, @Valid @RequestBody EnderecoProjetoRequest request) {
        return service.salvarEndereco(id, request);
    }

    @DeleteMapping("/{id}/endereco")
    @Operation(summary = "Excluir endereço do projeto", description = "Remove o endereço dedicado do projeto.")
    @ApiResponse(responseCode = "204", description = "Endereço removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto ou endereço não encontrado")
    public ResponseEntity<Void> excluirEndereco(@PathVariable Integer id) {
        service.excluirEndereco(id);
        return ResponseEntity.noContent().build();
    }

    // -------- Relacionamentos V8 do projeto --------

    @GetMapping("/{id}/fornecedores")
    @Operation(summary = "Listar fornecedores do projeto", description = "Retorna os fornecedores associados ao projeto via fornecedor_projeto.")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores vinculados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FornecedorProjetoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    public List<FornecedorProjetoResponse> listarFornecedores(@PathVariable Integer id) {
        return service.listarFornecedores(id);
    }

    @GetMapping("/{id}/fases")
    @Operation(summary = "Listar fases e tarefas do projeto", description = "Retorna os vínculos de projeto, fase e tarefa da tabela tarefa_fase.")
    @ApiResponse(responseCode = "200", description = "Lista de associações tarefa-fase", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarefaFaseResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    public List<TarefaFaseResponse> listarFases(@PathVariable Integer id) {
        return service.listarFases(id);
    }
}
