package com.faisca.backend.tarefa.service;

import com.faisca.backend.tarefa.dto.PrioridadeRequest;
import com.faisca.backend.tarefa.dto.PrioridadeResponse;
import com.faisca.backend.tarefa.entity.Prioridade;
import com.faisca.backend.tarefa.repository.PrioridadeRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrioridadeService {

    private final PrioridadeRepository repository;
    private final TarefaRepository tarefas;

    public PrioridadeService(PrioridadeRepository repository, TarefaRepository tarefas) {
        this.repository = repository;
        this.tarefas = tarefas;
    }

    @Transactional(readOnly = true)
    public List<PrioridadeResponse> listar() {
        return repository.findAllByOrderByIdAsc().stream().map(PrioridadeResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public PrioridadeResponse buscar(Integer id) {
        return new PrioridadeResponse(obter(id));
    }

    public PrioridadeResponse criar(PrioridadeRequest request) {
        String nome = request.getNome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prioridade já cadastrada");
        }
        Prioridade p = new Prioridade();
        p.setNome(nome);
        return new PrioridadeResponse(repository.save(p));
    }

    public PrioridadeResponse atualizar(Integer id, PrioridadeRequest request) {
        Prioridade p = obter(id);
        String nome = request.getNome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prioridade já cadastrada");
        }
        p.setNome(nome);
        return new PrioridadeResponse(repository.save(p));
    }

    public void excluir(Integer id) {
        Prioridade p = obter(id);
        if (tarefas.existsByPrioridadeRefId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prioridade vinculada a tarefas");
        }
        repository.delete(p);
    }

    public Prioridade obterOuCriarPorNome(String nomeInformado) {
        String limpo = nomeInformado.trim();
        String normalizado = normalizarNome(limpo);
        return repository.findByNomeIgnoreCase(normalizado)
                .or(() -> !normalizado.equalsIgnoreCase(limpo) ? repository.findByNomeIgnoreCase(limpo) : Optional.empty())
                .orElseGet(() -> {
                    Prioridade nova = new Prioridade();
                    nova.setNome(normalizado);
                    return repository.save(nova);
                });
    }

    public static String normalizarNome(String nome) {
        String limpo = nome.trim();
        if (limpo.equalsIgnoreCase("Media")) return "Média";
        if (limpo.equalsIgnoreCase("Critica")) return "Crítica";
        return limpo;
    }

    private Prioridade obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prioridade não encontrada"));
    }
}
