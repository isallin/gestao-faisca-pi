package com.faisca.backend.usuario.controller;

import com.faisca.backend.usuario.dto.*;
import com.faisca.backend.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Cadastro e manutenção de usuários")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<UsuarioResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar usuário", description = "Obtém um usuário pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public UsuarioResponse buscar(@Parameter(description = "ID do usuário", example = "1") @PathVariable Integer id) { return service.buscar(id); }
    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cria uma conta; operação pública.")
    @ApiResponse(responseCode = "201", description = "Usuário criado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioCadastroRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar usuário", description = "Substitui nome, sobrenome, e-mail e cargo.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    public UsuarioResponse atualizar(@PathVariable Integer id, @RequestBody @Valid UsuarioPutRequest request) {
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Alterar usuário parcialmente", description = "Altera apenas os campos informados.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    public UsuarioResponse atualizarParcial(@PathVariable Integer id, @RequestBody @Valid UsuarioPatchRequest request) {
        return service.atualizarParcial(id, request);
    }
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário, exceto a própria conta ou a última conta.")
    @ApiResponse(responseCode = "204", description = "Usuário removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "409", description = "Própria conta ou última conta")
    public ResponseEntity<Void> excluir(@Parameter(description = "ID do usuário", example = "1") @PathVariable Integer id, HttpServletRequest request) {
        service.excluir(id, (Integer) request.getAttribute("usuarioId"));
        return ResponseEntity.noContent().build();
    }
}
