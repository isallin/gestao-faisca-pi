package com.faisca.backend.fornecedor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@Tag(name = "Fornecedores", description = "Cadastro de fornecedores")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorController {
    private final FornecedorRepository repository;
    public FornecedorController(FornecedorRepository repository) { this.repository = repository; }

    @GetMapping
    @Operation(summary = "Listar fornecedores", description = "Retorna todos os fornecedores cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Fornecedor.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<Fornecedor> listar() { return repository.findAll(); }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor", description = "Consulta fornecedor pelo ID.")
    @ApiResponse(responseCode = "200", description = "Fornecedor encontrado", content = @Content(schema = @Schema(implementation = Fornecedor.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public Fornecedor buscar(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));
    }
    @PostMapping
    @Operation(summary = "Cadastrar fornecedor", description = "Cria um fornecedor com nome e categoria obrigatórios.")
    @ApiResponse(responseCode = "201", description = "Fornecedor criado", content = @Content(schema = @Schema(implementation = Fornecedor.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public ResponseEntity<Fornecedor> criar(@Valid @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(fornecedor));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor", description = "Substitui os dados de contato e categoria de um fornecedor.")
    @ApiResponse(responseCode = "200", description = "Fornecedor atualizado", content = @Content(schema = @Schema(implementation = Fornecedor.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public Fornecedor atualizar(@PathVariable Integer id, @Valid @RequestBody Fornecedor dados) {
        Fornecedor fornecedor = buscar(id);
        fornecedor.setName(dados.getName()); fornecedor.setCategory(dados.getCategory());
        fornecedor.setPhone(dados.getPhone()); fornecedor.setPhone2(dados.getPhone2());
        fornecedor.setEmail(dados.getEmail()); fornecedor.setEmail2(dados.getEmail2());
        fornecedor.setCity(dados.getCity()); fornecedor.setProjects(dados.getProjects());
        fornecedor.setNote(dados.getNote());
        return repository.save(fornecedor);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir fornecedor", description = "Remove fornecedor pelo ID.")
    @ApiResponse(responseCode = "204", description = "Fornecedor removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        repository.delete(buscar(id));
        return ResponseEntity.noContent().build();
    }
}
