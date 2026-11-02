package com.faisca.backend.projeto.service;

import com.faisca.backend.cliente.repository.ClienteRepository;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.projeto.dto.*;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class ProjetoService {
    private final ProjetoRepository repository;
    private final ClienteRepository clientes;
    private final FaseRepository fases;
    private final TarefaRepository tarefas;
    public ProjetoService(ProjetoRepository repository, ClienteRepository clientes, FaseRepository fases, TarefaRepository tarefas) {
        this.repository = repository; this.clientes = clientes; this.fases = fases; this.tarefas = tarefas;
    }
    @Transactional(readOnly = true)
    public List<ProjetoResponse> listar() { return repository.findAllByOrderByCodigoAsc().stream().map(ProjetoResponse::new).toList(); }
    @Transactional(readOnly = true)
    public ProjetoResponse buscar(Integer id) { return new ProjetoResponse(obter(id)); }
    public ProjetoResponse criar(ProjetoCadastroRequest request) {
        Projeto projeto = new Projeto();
        preencher(projeto, request);
        return new ProjetoResponse(repository.save(projeto));
    }
    public ProjetoResponse atualizar(Integer id, ProjetoCadastroRequest request) {
        Projeto projeto = obter(id);
        preencher(projeto, request);
        return new ProjetoResponse(repository.save(projeto));
    }
    public void excluir(Integer id) {
        Projeto projeto = obter(id);
        if (tarefas.existsByProjetoId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Projeto possui tarefas");
        repository.delete(projeto);
    }
    private Projeto obter(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }
    private void preencher(Projeto projeto, ProjetoCadastroRequest r) {
        if (r.getDataInicio() != null && r.getDataFim() != null && r.getDataFim().isBefore(r.getDataInicio()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fim anterior ao início");
        projeto.setCliente(clientes.findById(r.getClienteId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")));
        if (r.getFkTarefaFase() != null && !fases.existsById(r.getFkTarefaFase()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase não encontrada");
        projeto.setCodigo(r.getCodigo().trim()); projeto.setNome(r.getNome().trim());
        projeto.setRelacaoProprietario(trim(r.getRelacaoProprietario())); projeto.setTipoImovel(trim(r.getTipoImovel()));
        projeto.setAreaM2(r.getAreaM2()); projeto.setUnidadeNumero(trim(r.getUnidadeNumero()));
        projeto.setCondominio(trim(r.getCondominio())); projeto.setMatricula(trim(r.getMatricula()));
        projeto.setDataInicio(r.getDataInicio()); projeto.setDataFim(r.getDataFim());
        projeto.setObservacao(trim(r.getObservacao())); projeto.setFkTarefaFase(r.getFkTarefaFase());
    }
    private String trim(String s) { return s == null ? null : s.trim(); }
}
