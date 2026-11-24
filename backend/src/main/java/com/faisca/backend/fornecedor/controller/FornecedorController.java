package com.faisca.backend.fornecedor.controller;

import com.faisca.backend.fornecedor.dto.*;
import com.faisca.backend.fornecedor.service.FornecedorService;
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
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Empresas fornecedoras, categorias, contatos e vínculos com projetos (V8)")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorController {

    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar fornecedores", description = "Retorna todos os fornecedores cadastrados com categorias, contatos e projetos vinculados.")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FornecedorResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<FornecedorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor", description = "Consulta fornecedor pelo ID com seus relacionamentos V8.")
    @ApiResponse(responseCode = "200", description = "Fornecedor encontrado", content = @Content(schema = @Schema(implementation = FornecedorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public FornecedorResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar fornecedor", description = "Cria um fornecedor com categorias, contatos e projetos vinculados (compatível com formato legado e V8).")
    @ApiResponse(responseCode = "201", description = "Fornecedor criado", content = @Content(schema = @Schema(implementation = FornecedorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor", description = "Atualiza os dados cadastrais, categorias, contatos e projetos de um fornecedor.")
    @ApiResponse(responseCode = "200", description = "Fornecedor atualizado", content = @Content(schema = @Schema(implementation = FornecedorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public FornecedorResponse atualizar(@PathVariable Integer id, @Valid @RequestBody FornecedorRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir fornecedor", description = "Remove fornecedor e seus contatos/associações pelo ID.")
    @ApiResponse(responseCode = "204", description = "Fornecedor removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // -------- Categorias (fornecedor_categoria) --------

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias de fornecedor", description = "Retorna todas as categorias disponíveis para fornecedores.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FornecedorCategoriaResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<FornecedorCategoriaResponse> listarCategorias() {
        return service.listarCategorias();
    }

    @PostMapping("/categorias")
    @Operation(summary = "Criar categoria de fornecedor", description = "Cadastra uma nova categoria de fornecedor.")
    @ApiResponse(responseCode = "201", description = "Categoria criada", content = @Content(schema = @Schema(implementation = FornecedorCategoriaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    public ResponseEntity<FornecedorCategoriaResponse> criarCategoria(@Valid @RequestBody FornecedorCategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarCategoria(request));
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Atualizar categoria de fornecedor", description = "Renomeia uma categoria de fornecedor existente.")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada", content = @Content(schema = @Schema(implementation = FornecedorCategoriaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    public FornecedorCategoriaResponse atualizarCategoria(@PathVariable Integer id, @Valid @RequestBody FornecedorCategoriaRequest request) {
        return service.atualizarCategoria(id, request);
    }

    @DeleteMapping("/categorias/{id}")
    @Operation(summary = "Excluir categoria de fornecedor", description = "Remove uma categoria de fornecedor sem vínculos.")
    @ApiResponse(responseCode = "204", description = "Categoria removida")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Categoria vinculada a fornecedores")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Integer id) {
        service.excluirCategoria(id);
        return ResponseEntity.noContent().build();
    }

    // -------- Contatos (fornecedor_contato) --------

    @GetMapping("/{fornecedorId}/contatos")
    @Operation(summary = "Listar contatos do fornecedor", description = "Retorna todos os contatos cadastrados para um fornecedor.")
    @ApiResponse(responseCode = "200", description = "Lista de contatos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FornecedorContatoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public List<FornecedorContatoResponse> listarContatos(@PathVariable Integer fornecedorId) {
        return service.listarContatos(fornecedorId);
    }

    @PostMapping("/{fornecedorId}/contatos")
    @Operation(summary = "Adicionar contato ao fornecedor", description = "Cria um contato vinculado ao fornecedor informado.")
    @ApiResponse(responseCode = "201", description = "Contato criado", content = @Content(schema = @Schema(implementation = FornecedorContatoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public ResponseEntity<FornecedorContatoResponse> criarContato(@PathVariable Integer fornecedorId,
                                                                  @Valid @RequestBody FornecedorContatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarContato(fornecedorId, request));
    }

    @PutMapping("/{fornecedorId}/contatos/{contatoId}")
    @Operation(summary = "Atualizar contato do fornecedor", description = "Altera dados de um contato do fornecedor.")
    @ApiResponse(responseCode = "200", description = "Contato atualizado", content = @Content(schema = @Schema(implementation = FornecedorContatoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor ou contato não encontrado")
    public FornecedorContatoResponse atualizarContato(@PathVariable Integer fornecedorId,
                                                      @PathVariable Integer contatoId,
                                                      @Valid @RequestBody FornecedorContatoRequest request) {
        return service.atualizarContato(fornecedorId, contatoId, request);
    }

    @DeleteMapping("/{fornecedorId}/contatos/{contatoId}")
    @Operation(summary = "Excluir contato do fornecedor", description = "Remove um contato do fornecedor.")
    @ApiResponse(responseCode = "204", description = "Contato removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor ou contato não encontrado")
    public ResponseEntity<Void> excluirContato(@PathVariable Integer fornecedorId, @PathVariable Integer contatoId) {
        service.excluirContato(fornecedorId, contatoId);
        return ResponseEntity.noContent().build();
    }

    // -------- Projetos vinculados (fornecedor_projeto) --------

    @GetMapping("/{fornecedorId}/projetos")
    @Operation(summary = "Listar projetos do fornecedor", description = "Retorna os vínculos de serviço entre o fornecedor e projetos.")
    @ApiResponse(responseCode = "200", description = "Lista de vínculos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FornecedorProjetoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public List<FornecedorProjetoResponse> listarProjetos(@PathVariable Integer fornecedorId) {
        return service.listarProjetos(fornecedorId);
    }

    @PostMapping("/{fornecedorId}/projetos")
    @Operation(summary = "Vincular fornecedor a projeto", description = "Associa ou atualiza a prestação de serviço do fornecedor em um projeto.")
    @ApiResponse(responseCode = "201", description = "Vínculo criado", content = @Content(schema = @Schema(implementation = FornecedorProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados ou datas inválidas")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor ou projeto não encontrado")
    public ResponseEntity<FornecedorProjetoResponse> vincularProjeto(@PathVariable Integer fornecedorId,
                                                                     @Valid @RequestBody FornecedorProjetoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.vincularProjeto(fornecedorId, request));
    }

    @PutMapping("/{fornecedorId}/projetos/{projetoId}")
    @Operation(summary = "Atualizar vínculo fornecedor-projeto", description = "Atualiza serviço, valor e período do fornecedor no projeto.")
    @ApiResponse(responseCode = "200", description = "Vínculo atualizado", content = @Content(schema = @Schema(implementation = FornecedorProjetoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados ou datas inválidas")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Vínculo não encontrado")
    public FornecedorProjetoResponse atualizarVinculoProjeto(@PathVariable Integer fornecedorId,
                                                             @PathVariable Integer projetoId,
                                                             @Valid @RequestBody FornecedorProjetoRequest request) {
        return service.atualizarVinculoProjeto(fornecedorId, projetoId, request);
    }

    @DeleteMapping("/{fornecedorId}/projetos/{projetoId}")
    @Operation(summary = "Desvincular fornecedor de projeto", description = "Remove a associação entre o fornecedor e o projeto.")
    @ApiResponse(responseCode = "204", description = "Vínculo removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Vínculo não encontrado")
    public ResponseEntity<Void> desvincularProjeto(@PathVariable Integer fornecedorId, @PathVariable Integer projetoId) {
        service.desvincularProjeto(fornecedorId, projetoId);
        return ResponseEntity.noContent().build();
    }
}
