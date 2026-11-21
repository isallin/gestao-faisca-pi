package com.faisca.backend.tarefa.service;

import com.faisca.backend.calendario.service.CalendarSyncService;
import com.faisca.backend.calendario.service.EventoCalendario;
import com.faisca.backend.calendario.service.SyncCalendarioEvent;
import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.*;
import com.faisca.backend.tarefa.repository.*;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class TarefaService {
    private final TarefaRepository repository;
    private final ProjetoRepository projetos;
    private final TarefaCategoriaRepository categorias;
    private final FaseRepository fases;
    private final UsuarioRepository usuarios;
    private final CalendarSyncService calendario;
    public TarefaService(TarefaRepository repository, ProjetoRepository projetos, TarefaCategoriaRepository categorias, FaseRepository fases, UsuarioRepository usuarios, CalendarSyncService calendario) {
        this.repository = repository; this.projetos = projetos; this.categorias = categorias; this.fases = fases; this.usuarios = usuarios; this.calendario = calendario;
    }
    @Transactional(readOnly = true)
    public List<TarefaResponse> listar() { return repository.findAll().stream().map(TarefaResponse::new).toList(); }
    @Transactional(readOnly = true)
    public TarefaResponse buscar(Integer id) { return new TarefaResponse(obter(id)); }
    @Transactional(readOnly = true)
    public List<TarefaCategoria> listarCategorias() { return categorias.findAll(); }
    @Transactional(readOnly = true)
    public List<Fase> listarFases() { return fases.findAll(); }
    public TarefaResponse criar(TarefaCadastroRequest r, Integer usuarioId) {
        Tarefa tarefa = new Tarefa();
        preencher(tarefa, r);
        tarefa.setCriadoPor(usuarioId);
        tarefa.adicionarStatus(novoStatus(r.getStatus()));
        TarefaResponse response = new TarefaResponse(repository.saveAndFlush(tarefa));
        calendario.publicarCriacao(SyncCalendarioEvent.TipoEntidade.TAREFA, tarefa.getId(), usuarioId, EventoCalendario.deTarefa(tarefa));
        return response;
    }
    public TarefaResponse atualizar(Integer id, TarefaCadastroRequest r) {
        Tarefa tarefa = obter(id);
        preencher(tarefa, r);
        if (!r.getStatus().equals(tarefa.getStatusAtual())) tarefa.adicionarStatus(novoStatus(r.getStatus()));
        repository.flush();
        TarefaResponse response = new TarefaResponse(tarefa);
        calendario.publicarAtualizacao(SyncCalendarioEvent.TipoEntidade.TAREFA, id, EventoCalendario.deTarefa(tarefa));
        return response;
    }
    public TarefaResponse atualizarStatus(Integer id, TarefaStatusRequest r) {
        Tarefa tarefa = obter(id);
        if (!r.getStatus().equals(tarefa.getStatusAtual())) {
            tarefa.adicionarStatus(novoStatus(r.getStatus()));
            repository.flush();
            calendario.publicarAtualizacao(SyncCalendarioEvent.TipoEntidade.TAREFA, id, EventoCalendario.deTarefa(tarefa));
        }
        return new TarefaResponse(tarefa);
    }
    public void excluir(Integer id) {
        Tarefa tarefa = obter(id);
        for (RegistroHoras registro : tarefa.getRegistrosHoras()) {
            calendario.publicarExclusao(SyncCalendarioEvent.TipoEntidade.REGISTRO_HORAS, registro.getId());
        }
        repository.delete(tarefa);
        calendario.publicarExclusao(SyncCalendarioEvent.TipoEntidade.TAREFA, id);
    }
    private Tarefa obter(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }
    private StatusTarefa novoStatus(String valor) {
        StatusTarefa status = new StatusTarefa(); status.setStatus(valor.trim()); return status;
    }
    private void preencher(Tarefa tarefa, TarefaCadastroRequest r) {
        if (r.getDataFimPrev().isBefore(r.getDataInicioPrev()) || (r.getDataInicioReal() != null && r.getDataFimReal() != null && r.getDataFimReal().isBefore(r.getDataInicioReal())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fim anterior ao início");
        tarefa.setProjeto(projetos.findById(r.getProjetoId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado")));
        tarefa.setCategoria(categorias.findById(r.getCategoriaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada")));
        if (!fases.existsById(r.getFkTarefaFase())) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase não encontrada");
        tarefa.setFkTarefaFase(r.getFkTarefaFase()); tarefa.setNome(r.getNome().trim());
        tarefa.setPrioridade(r.getPrioridade()); tarefa.setDataInicioPrev(r.getDataInicioPrev());
        tarefa.setDataFimPrev(r.getDataFimPrev()); tarefa.setDataInicioReal(r.getDataInicioReal());
        tarefa.setDataFimReal(r.getDataFimReal()); tarefa.setObservacao(r.getObservacao() == null ? null : r.getObservacao().trim());
        tarefa.getColaboradores().clear();
        if (r.getColaboradorId() != null) tarefa.getColaboradores().add(usuarios.findById(r.getColaboradorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado")));
        tarefa.getChecklist().clear();
        if (r.getChecklist() != null) for (TarefaCadastroRequest.ChecklistRequest item : r.getChecklist()) {
            ChecklistItem checklistItem = new ChecklistItem();
            checklistItem.setTarefa(tarefa); checklistItem.setText(item.text().trim()); checklistItem.setDone(item.done());
            tarefa.getChecklist().add(checklistItem);
        }
    }
}
