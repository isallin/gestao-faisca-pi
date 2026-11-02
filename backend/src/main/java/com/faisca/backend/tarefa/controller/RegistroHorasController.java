package com.faisca.backend.tarefa.controller;

import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/registros")
public class RegistroHorasController {
    private final RegistroHorasRepository registros;
    private final TarefaRepository tarefas;
    public RegistroHorasController(RegistroHorasRepository registros, TarefaRepository tarefas) {
        this.registros = registros; this.tarefas = tarefas;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<RegistroHorasResponse> listar() { return registros.findAll().stream().map(RegistroHorasResponse::new).toList(); }
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RegistroHorasResponse buscar(@PathVariable Integer id) { return new RegistroHorasResponse(obter(id)); }
    @PostMapping
    @Transactional
    public ResponseEntity<RegistroHorasResponse> criar(@Valid @RequestBody RegistroHorasRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salvar(new RegistroHoras(), request));
    }
    @PutMapping("/{id}")
    @Transactional
    public RegistroHorasResponse atualizar(@PathVariable Integer id, @Valid @RequestBody RegistroHorasRequest request) {
        return salvar(obter(id), request);
    }
    @DeleteMapping("/{id}")
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
