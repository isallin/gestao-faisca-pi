package com.faisca.backend.projeto.service;

import com.faisca.backend.calendario.service.CalendarSyncService;
import com.faisca.backend.calendario.service.EventoCalendario;
import com.faisca.backend.calendario.service.SyncCalendarioEvent;
import com.faisca.backend.cliente.repository.ClienteRepository;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.fornecedor.dto.FornecedorProjetoResponse;
import com.faisca.backend.fornecedor.repository.FornecedorProjetoRepository;
import com.faisca.backend.projeto.dto.*;
import com.faisca.backend.projeto.entity.EnderecoProjeto;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.EnderecoProjetoRepository;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.dto.TarefaFaseResponse;
import com.faisca.backend.tarefa.repository.TarefaFaseRepository;
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
    private final EnderecoProjetoRepository enderecos;
    private final FornecedorProjetoRepository fornecedorProjetos;
    private final TarefaFaseRepository tarefaFases;
    private final CalendarSyncService calendario;

    public ProjetoService(ProjetoRepository repository,
                          ClienteRepository clientes,
                          FaseRepository fases,
                          TarefaRepository tarefas,
                          EnderecoProjetoRepository enderecos,
                          FornecedorProjetoRepository fornecedorProjetos,
                          TarefaFaseRepository tarefaFases,
                          CalendarSyncService calendario) {
        this.repository = repository;
        this.clientes = clientes;
        this.fases = fases;
        this.tarefas = tarefas;
        this.enderecos = enderecos;
        this.fornecedorProjetos = fornecedorProjetos;
        this.tarefaFases = tarefaFases;
        this.calendario = calendario;
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponse> listar() {
        return repository.findAllByOrderByCodigoAsc().stream().map(ProjetoResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public ProjetoResponse buscar(Integer id) {
        return new ProjetoResponse(obter(id));
    }

    public ProjetoResponse criar(ProjetoCadastroRequest request, Integer usuarioId) {
        Projeto projeto = new Projeto();
        preencher(projeto, request);
        projeto.setCriadoPor(usuarioId);
        Projeto salvo = repository.saveAndFlush(projeto);
        EventoCalendario.deProjeto(salvo).ifPresent(dados ->
                calendario.publicarCriacao(SyncCalendarioEvent.TipoEntidade.PROJETO, salvo.getId(), usuarioId, dados));
        return new ProjetoResponse(salvo);
    }

    public ProjetoResponse atualizar(Integer id, ProjetoCadastroRequest request) {
        Projeto projeto = obter(id);
        preencher(projeto, request);
        Projeto salvo = repository.saveAndFlush(projeto);
        EventoCalendario.deProjeto(salvo).ifPresent(dados ->
                calendario.publicarAtualizacao(SyncCalendarioEvent.TipoEntidade.PROJETO, id, dados));
        return new ProjetoResponse(salvo);
    }

    public void excluir(Integer id) {
        Projeto projeto = obter(id);
        if (tarefas.existsByProjetoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Projeto possui tarefas");
        }
        if (fornecedorProjetos.existsByProjetoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Projeto possui fornecedores vinculados");
        }
        repository.delete(projeto);
        calendario.publicarExclusao(SyncCalendarioEvent.TipoEntidade.PROJETO, id);
    }

    // -------- Endereço dedicado (endereco_projeto V8) --------

    @Transactional(readOnly = true)
    public EnderecoProjetoResponse buscarEndereco(Integer projetoId) {
        obter(projetoId);
        EnderecoProjeto ep = enderecos.findByProjetoId(projetoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço do projeto não encontrado"));
        return new EnderecoProjetoResponse(ep);
    }

    public EnderecoProjetoResponse salvarEndereco(Integer projetoId, EnderecoProjetoRequest request) {
        Projeto projeto = obter(projetoId);
        EnderecoProjeto ep = projeto.getEnderecoProjeto();
        if (ep == null) {
            ep = new EnderecoProjeto();
            projeto.setEnderecoProjeto(ep);
        }
        String cep = digits(request.getCep());
        String logradouro = trim(request.getLogradouro());
        String numero = trim(request.getNumero());
        String complemento = trim(request.getComplemento());
        String bairro = trim(request.getBairro());
        String cidade = trim(request.getCidade());
        String estado = request.getEstado() == null || request.getEstado().isBlank() ? null : request.getEstado().trim().toUpperCase();
        String pais = request.getPais() == null || request.getPais().isBlank() ? "Brasil" : request.getPais().trim();

        ep.setCep(cep);
        ep.setLogradouro(logradouro);
        ep.setNumero(numero);
        ep.setComplemento(complemento);
        ep.setBairro(bairro);
        ep.setCidade(cidade);
        ep.setEstado(estado);
        ep.setPais(pais);

        // Mantém colunas diretas em projeto sincronizadas (V8 + compatibilidade)
        projeto.setCep(cep);
        projeto.setLogradouro(logradouro);
        projeto.setNumero(numero);
        projeto.setComplemento(complemento);
        projeto.setBairro(bairro);
        projeto.setCidade(cidade);
        projeto.setEstado(estado);
        projeto.setPais(pais);

        repository.saveAndFlush(projeto);
        return new EnderecoProjetoResponse(projeto.getEnderecoProjeto());
    }

    public void excluirEndereco(Integer projetoId) {
        Projeto projeto = obter(projetoId);
        if (projeto.getEnderecoProjeto() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço do projeto não encontrado");
        }
        projeto.setEnderecoProjeto(null);
        projeto.setCep(null);
        projeto.setLogradouro(null);
        projeto.setNumero(null);
        projeto.setComplemento(null);
        projeto.setBairro(null);
        projeto.setCidade(null);
        projeto.setEstado(null);
        projeto.setPais(null);
        repository.saveAndFlush(projeto);
    }

    @Transactional(readOnly = true)
    public List<FornecedorProjetoResponse> listarFornecedores(Integer projetoId) {
        obter(projetoId);
        return fornecedorProjetos.findByProjetoId(projetoId).stream().map(FornecedorProjetoResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public List<TarefaFaseResponse> listarFases(Integer projetoId) {
        obter(projetoId);
        return tarefaFases.findByIdIdProjeto(projetoId).stream().map(TarefaFaseResponse::new).toList();
    }

    // -------- Auxiliares --------

    private Projeto obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    private void preencher(Projeto projeto, ProjetoCadastroRequest r) {
        if (r.getDataInicio() != null && r.getDataFim() != null && r.getDataFim().isBefore(r.getDataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fim anterior ao início");
        }
        projeto.setCliente(clientes.findById(r.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")));
        if (r.getFkTarefaFase() != null && !fases.existsById(r.getFkTarefaFase())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase não encontrada");
        }
        projeto.setCodigo(r.getCodigo().trim());
        projeto.setNome(r.getNome().trim());
        projeto.setRelacaoProprietario(trim(r.getRelacaoProprietario()));
        projeto.setTipoImovel(trim(r.getTipoImovel()));
        projeto.setAreaM2(r.getAreaM2());
        projeto.setUnidadeNumero(trim(r.getUnidadeNumero()));
        projeto.setCondominio(trim(r.getCondominio()));
        projeto.setMatricula(trim(r.getMatricula()));

        String cep = digits(r.getCep());
        String logradouro = trim(r.getLogradouro());
        String numero = !blank(r.getNumero()) ? r.getNumero().trim() : (!blank(r.getUnidadeNumero()) && r.getUnidadeNumero().trim().length() <= 10 ? r.getUnidadeNumero().trim() : null);
        String complemento = trim(r.getComplemento());
        String bairro = trim(r.getBairro());
        String cidade = trim(r.getCidade());
        String estado = blank(r.getEstado()) ? null : r.getEstado().trim().toUpperCase();
        String pais = trim(r.getPais());

        projeto.setCep(cep);
        projeto.setLogradouro(logradouro);
        projeto.setNumero(numero);
        projeto.setComplemento(complemento);
        projeto.setBairro(bairro);
        projeto.setCidade(cidade);
        projeto.setEstado(estado);
        projeto.setPais(pais);
        projeto.setDataInicio(r.getDataInicio());
        projeto.setDataFim(r.getDataFim());
        projeto.setObservacao(trim(r.getObservacao()));
        projeto.setFkTarefaFase(r.getFkTarefaFase());

        boolean possuiEndereco = !blank(cep) || !blank(logradouro) || !blank(numero)
                || !blank(complemento) || !blank(bairro) || !blank(cidade) || !blank(estado);
        if (possuiEndereco) {
            EnderecoProjeto ep = projeto.getEnderecoProjeto();
            if (ep == null) {
                ep = new EnderecoProjeto();
                projeto.setEnderecoProjeto(ep);
            }
            ep.setCep(cep);
            ep.setLogradouro(logradouro);
            ep.setNumero(numero);
            ep.setComplemento(complemento);
            ep.setBairro(bairro);
            ep.setCidade(cidade);
            ep.setEstado(estado);
            ep.setPais(blank(pais) ? "Brasil" : pais);
        }
    }

    private String digits(String s) {
        if (blank(s)) return null;
        String d = s.replaceAll("\\D", "");
        return d.isEmpty() ? null : d;
    }

    private String trim(String s) { return blank(s) ? null : s.trim(); }
    private boolean blank(String s) { return s == null || s.isBlank(); }
}
