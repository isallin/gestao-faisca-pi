package com.faisca.backend.fase.service;

import com.faisca.backend.fase.dto.FaseRequest;
import com.faisca.backend.fase.dto.FaseResponse;
import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.repository.TarefaFaseRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class FaseService {

    private final FaseRepository repository;
    private final ProjetoRepository projetos;
    private final TarefaRepository tarefas;
    private final TarefaFaseRepository tarefaFases;

    public FaseService(FaseRepository repository,
                       ProjetoRepository projetos,
                       TarefaRepository tarefas,
                       TarefaFaseRepository tarefaFases) {
        this.repository = repository;
        this.projetos = projetos;
        this.tarefas = tarefas;
        this.tarefaFases = tarefaFases;
    }

    @Transactional(readOnly = true)
    public List<FaseResponse> listar() {
        return repository.findAllByOrderByOrdemAsc().stream().map(FaseResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public FaseResponse buscar(Integer id) {
        return new FaseResponse(obter(id));
    }

    public FaseResponse criar(FaseRequest request) {
        String nome = request.getNome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fase já cadastrada");
        }
        Fase fase = new Fase();
        fase.setNome(nome);
        fase.setOrdem(request.getOrdem() != null ? request.getOrdem() : (int) repository.count() + 1);
        return new FaseResponse(repository.save(fase));
    }

    public FaseResponse atualizar(Integer id, FaseRequest request) {
        Fase fase = obter(id);
        String nome = request.getNome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fase já cadastrada");
        }
        fase.setNome(nome);
        if (request.getOrdem() != null) {
            fase.setOrdem(request.getOrdem());
        }
        return new FaseResponse(repository.save(fase));
    }

    public void excluir(Integer id) {
        Fase fase = obter(id);
        if (projetos.existsByFkTarefaFase(id) || tarefas.existsByFkTarefaFase(id) || tarefaFases.existsByIdIdFase(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fase vinculada a projetos ou tarefas");
        }
        repository.delete(fase);
    }

    private Fase obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase não encontrada"));
    }
}
