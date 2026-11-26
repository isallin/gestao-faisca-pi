package com.faisca.backend.cliente.controller;

import com.faisca.backend.cliente.dto.*;
import com.faisca.backend.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Pessoas físicas, jurídicas, endereços e representantes legais (V8)")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna os clientes e seus dados resumidos.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClienteResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<ClienteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente", description = "Retorna os dados de um cliente pelo ID.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(schema = @Schema(implementation = ClienteResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ClienteResponse buscar(@Parameter(description = "ID do cliente", example = "1") @PathVariable Integer id) {
        return service.buscar(id);
    }

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
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // -------- Representantes Legais (V8) --------

    @GetMapping("/{clienteId}/representantes")
    @Operation(summary = "Listar representantes legais do cliente PJ", description = "Retorna todos os representantes legais cadastrados para um cliente pessoa jurídica.")
    @ApiResponse(responseCode = "200", description = "Lista de representantes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RepresentanteLegalResponse.class))))
    @ApiResponse(responseCode = "400", description = "Cliente não é PJ")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public List<RepresentanteLegalResponse> listarRepresentantes(@PathVariable Integer clienteId) {
        return service.listarRepresentantes(clienteId);
    }

    @PostMapping("/{clienteId}/representantes")
    @Operation(summary = "Cadastrar representante legal", description = "Adiciona um representante legal ao cliente PJ.")
    @ApiResponse(responseCode = "201", description = "Representante criado", content = @Content(schema = @Schema(implementation = RepresentanteLegalResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não é PJ")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<RepresentanteLegalResponse> criarRepresentante(@PathVariable Integer clienteId,
                                                                         @Valid @RequestBody RepresentanteLegalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarRepresentante(clienteId, request));
    }

    @PutMapping("/{clienteId}/representantes/{representanteId}")
    @Operation(summary = "Atualizar representante legal", description = "Altera os dados de um representante legal do cliente PJ.")
    @ApiResponse(responseCode = "200", description = "Representante atualizado", content = @Content(schema = @Schema(implementation = RepresentanteLegalResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente não é PJ")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente ou representante não encontrado")
    public RepresentanteLegalResponse atualizarRepresentante(@PathVariable Integer clienteId,
                                                             @PathVariable Integer representanteId,
                                                             @Valid @RequestBody RepresentanteLegalRequest request) {
        return service.atualizarRepresentante(clienteId, representanteId, request);
    }

    @DeleteMapping("/{clienteId}/representantes/{representanteId}")
    @Operation(summary = "Excluir representante legal", description = "Remove um representante legal do cliente PJ.")
    @ApiResponse(responseCode = "204", description = "Representante removido")
    @ApiResponse(responseCode = "400", description = "Cliente não é PJ")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente ou representante não encontrado")
    public ResponseEntity<Void> excluirRepresentante(@PathVariable Integer clienteId,
                                                     @PathVariable Integer representanteId) {
        service.excluirRepresentante(clienteId, representanteId);
        return ResponseEntity.noContent().build();
    }

    // -------- Endereços do Cliente (V8) --------

    @GetMapping("/{clienteId}/enderecos")
    @Operation(summary = "Listar endereços do cliente", description = "Retorna todos os endereços cadastrados para um cliente.")
    @ApiResponse(responseCode = "200", description = "Lista de endereços", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnderecoClienteResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public List<EnderecoClienteResponse> listarEnderecos(@PathVariable Integer clienteId) {
        return service.listarEnderecos(clienteId);
    }

    @PostMapping("/{clienteId}/enderecos")
    @Operation(summary = "Cadastrar endereço do cliente", description = "Adiciona um novo endereço na tabela endereco_cliente.")
    @ApiResponse(responseCode = "201", description = "Endereço criado", content = @Content(schema = @Schema(implementation = EnderecoClienteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<EnderecoClienteResponse> criarEndereco(@PathVariable Integer clienteId,
                                                                 @Valid @RequestBody EnderecoClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarEndereco(clienteId, request));
    }

    @PutMapping("/{clienteId}/enderecos/{enderecoId}")
    @Operation(summary = "Atualizar endereço do cliente", description = "Atualiza os dados de um endereço do cliente.")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado", content = @Content(schema = @Schema(implementation = EnderecoClienteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente ou endereço não encontrado")
    public EnderecoClienteResponse atualizarEndereco(@PathVariable Integer clienteId,
                                                     @PathVariable Integer enderecoId,
                                                     @Valid @RequestBody EnderecoClienteRequest request) {
        return service.atualizarEndereco(clienteId, enderecoId, request);
    }

    @DeleteMapping("/{clienteId}/enderecos/{enderecoId}")
    @Operation(summary = "Excluir endereço do cliente", description = "Remove um endereço cadastrado para o cliente.")
    @ApiResponse(responseCode = "204", description = "Endereço removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente ou endereço não encontrado")
    public ResponseEntity<Void> excluirEndereco(@PathVariable Integer clienteId,
                                                @PathVariable Integer enderecoId) {
        service.excluirEndereco(clienteId, enderecoId);
        return ResponseEntity.noContent().build();
    }
}
