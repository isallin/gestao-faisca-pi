package com.faisca.backend.cliente.controller;

import com.faisca.backend.cliente.dto.*;
import com.faisca.backend.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Pessoas físicas e jurídicas")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna os clientes e seus dados resumidos.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClienteResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<ClienteResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente", description = "Retorna os dados de um cliente pelo ID.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ClienteResponse buscar(@Parameter(description = "ID do cliente", example = "1") @PathVariable Integer id) { return service.buscar(id); }
    @PostMapping
    @Operation(summary = "Cadastrar cliente", description = "Cria um cliente PF ou PJ com endereço e representantes opcionais.")
    @ApiResponse(responseCode = "201", description = "Cliente criado", content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome/documento ausentes")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "409", description = "Documento já cadastrado")
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados sem alterar o tipo PF/PJ.")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado", content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou tentativa de alterar tipo")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "409", description = "Documento já cadastrado")
    public ClienteResponse atualizar(@PathVariable Integer id, @RequestBody @Valid ClienteCadastroRequest request) {
        return service.atualizar(id, request);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove cliente sem projetos associados.")
    @ApiResponse(responseCode = "204", description = "Cliente removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "409", description = "Cliente possui projetos")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
