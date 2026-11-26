package com.faisca.backend.tarefa.controller;

import com.faisca.backend.tarefa.dto.RegistroHorasRequest;
import com.faisca.backend.tarefa.dto.RegistroHorasResponse;
import com.faisca.backend.tarefa.service.RegistroHorasService;
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
@RequestMapping("/api/registros")
@Tag(name = "Registros de horas", description = "Apontamento de horas em tarefas vinculados a colaborador_tarefa (V8)")
@SecurityRequirement(name = "bearerAuth")
public class RegistroHorasController {

    private final RegistroHorasService service;

    public RegistroHorasController(RegistroHorasService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar registros de horas", description = "Retorna todos os apontamentos de horas com filtro opcional por tarefa ou colaborador.")
    @ApiResponse(responseCode = "200", description = "Lista de registros", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistroHorasResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public List<RegistroHorasResponse> listar(
            @Parameter(description = "Filtrar pelo ID da tarefa") @RequestParam(required = false) Integer tarefaId,
            @Parameter(description = "Filtrar pelo ID do colaborador") @RequestParam(required = false) Integer colaboradorId) {
        return service.listar(tarefaId, colaboradorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de horas", description = "Consulta um apontamento pelo ID.")
    @ApiResponse(responseCode = "200", description = "Registro encontrado", content = @Content(schema = @Schema(implementation = RegistroHorasResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    public RegistroHorasResponse buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Criar registro de horas", description = "Registra intervalo de trabalho em uma tarefa garantindo vínculo em colaborador_tarefa; fim deve ser posterior ao início.")
    @ApiResponse(responseCode = "201", description = "Registro criado", content = @Content(schema = @Schema(implementation = RegistroHorasResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou intervalo incoerente")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa ou colaborador não encontrado")
    public ResponseEntity<RegistroHorasResponse> criar(@Valid @RequestBody RegistroHorasRequest request,
                                                       @RequestAttribute("usuarioId") Integer usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request, usuarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de horas", description = "Altera tarefa, colaborador, intervalo e descrição de um registro.")
    @ApiResponse(responseCode = "200", description = "Registro atualizado", content = @Content(schema = @Schema(implementation = RegistroHorasResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou intervalo incoerente")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Registro, tarefa ou colaborador não encontrado")
    public RegistroHorasResponse atualizar(@PathVariable Integer id,
                                           @Valid @RequestBody RegistroHorasRequest request,
                                           @RequestAttribute(value = "usuarioId", required = false) Integer usuarioId) {
        return service.atualizar(id, request, usuarioId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de horas", description = "Remove um apontamento pelo ID.")
    @ApiResponse(responseCode = "204", description = "Registro removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
