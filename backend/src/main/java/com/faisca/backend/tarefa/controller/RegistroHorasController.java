package com.faisca.backend.tarefa.controller;

import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.repository.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/registros")
@Tag(name = "Registros de horas", description = "Apontamento de horas em tarefas")
@SecurityRequirement(name = "bearerAuth")
public class RegistroHorasController {
    private final RegistroHorasRepository registros;
    private final TarefaRepository tarefas;
    public RegistroHorasController(RegistroHorasRepository registros, TarefaRepository tarefas) {
        this.registros = registros; this.tarefas = tarefas;
    }

    @GetMapping
    @Operation(summary = "Listar registros de horas", description = "Retorna todos os apontamentos de horas.")
    @ApiResponse(responseCode = "200", description = "Lista de registros", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistroHorasResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @Transactional(readOnly = true)
    public List<RegistroHorasResponse> listar() { return registros.findAll().stream().map(RegistroHorasResponse::new).toList(); }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de horas", description = "Consulta um apontamento pelo ID.")
    @ApiResponse(responseCode = "200", description = "Registro encontrado", content = @Content(schema = @Schema(implementation = RegistroHorasResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    @Transactional(readOnly = true)
    public RegistroHorasResponse buscar(@PathVariable Integer id) { return new RegistroHorasResponse(obter(id)); }
    @PostMapping
    @Operation(summary = "Criar registro de horas", description = "Registra intervalo de trabalho em uma tarefa; fim deve ser posterior ao início.")
    @ApiResponse(responseCode = "201", description = "Registro criado", content = @Content(schema = @Schema(implementation = RegistroHorasResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou intervalo incoerente")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    @Transactional
    public ResponseEntity<RegistroHorasResponse> criar(@Valid @RequestBody RegistroHorasRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salvar(new RegistroHoras(), request));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar registro de horas", description = "Altera tarefa, intervalo e descrição de um registro.")
    @ApiResponse(responseCode = "200", description = "Registro atualizado", content = @Content(schema = @Schema(implementation = RegistroHorasResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou intervalo incoerente")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Registro ou tarefa não encontrada")
    @Transactional
    public RegistroHorasResponse atualizar(@PathVariable Integer id, @Valid @RequestBody RegistroHorasRequest request) {
        return salvar(obter(id), request);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de horas", description = "Remove um apontamento pelo ID.")
    @ApiResponse(responseCode = "204", description = "Registro removido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        registros.delete(obter(id)); return ResponseEntity.noContent().build();
    }
    private RegistroHoras obter(Integer id) {
        return registros.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }
    private RegistroHorasResponse salvar(RegistroHoras registro, RegistroHorasRequest request) {
        if (!request.fim().isAfter(request.inicio()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fim deve ser posterior ao início");
        registro.setTarefa(tarefas.findById(request.tarefaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada")));
        registro.setHoraInicio(request.inicio()); registro.setHoraFim(request.fim());
        registro.setDescricao(request.descricao());
        return new RegistroHorasResponse(registros.save(registro));
    }
}
