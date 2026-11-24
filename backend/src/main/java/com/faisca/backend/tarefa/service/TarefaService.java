package com.faisca.backend.tarefa.service;

import com.faisca.backend.calendario.service.CalendarSyncService;
import com.faisca.backend.calendario.service.EventoCalendario;
import com.faisca.backend.calendario.service.SyncCalendarioEvent;
import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.fase.repository.FaseRepository;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.dto.*;
import com.faisca.backend.tarefa.entity.*;
import com.faisca.backend.tarefa.repository.*;
import com.faisca.backend.usuario.entity.Usuario;
import com.faisca.backend.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TarefaService {

    private final TarefaRepository repository;
    private final ProjetoRepository projetos;
    private final TarefaCategoriaRepository categorias;
    private final FaseRepository fases;
    private final UsuarioRepository usuarios;
    private final PrioridadeRepository prioridades;
    private final PrioridadeService prioridadeService;
    private final SubtarefaRepository subtarefas;
    private final ColaboradorTarefaRepository colaboradorTarefas;
    private final TarefaFaseRepository tarefaFases;
    private final RegistroHorasRepository registroHoras;
    private final CalendarSyncService calendario;

    public TarefaService(TarefaRepository repository,
                         ProjetoRepository projetos,
                         TarefaCategoriaRepository categorias,
                         FaseRepository fases,
                         UsuarioRepository usuarios,
                         PrioridadeRepository prioridades,
                         PrioridadeService prioridadeService,
                         SubtarefaRepository subtarefas,
                         ColaboradorTarefaRepository colaboradorTarefas,
                         TarefaFaseRepository tarefaFases,
                         RegistroHorasRepository registroHoras,
                         CalendarSyncService calendario) {
        this.repository = repository;
        this.projetos = projetos;
        this.categorias = categorias;
        this.fases = fases;
        this.usuarios = usuarios;
        this.prioridades = prioridades;
        this.prioridadeService = prioridadeService;
        this.subtarefas = subtarefas;
        this.colaboradorTarefas = colaboradorTarefas;
        this.tarefaFases = tarefaFases;
        this.registroHoras = registroHoras;
        this.calendario = calendario;
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar() {
        return repository.findAll().stream().map(TarefaResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public TarefaResponse buscar(Integer id) {
        return new TarefaResponse(obter(id));
    }

    @Transactional(readOnly = true)
    public List<TarefaCategoria> listarCategorias() {
        return categorias.findAll();
    }

    public TarefaCategoria criarCategoria(TarefaCategoriaRequest request) {
        String nome = request.getNome().trim();
        if (categorias.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria de tarefa já cadastrada");
        }
        TarefaCategoria cat = new TarefaCategoria();
        cat.setNome(nome);
        return categorias.save(cat);
    }

    public TarefaCategoria atualizarCategoria(Integer id, TarefaCategoriaRequest request) {
        TarefaCategoria cat = categorias.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        String nome = request.getNome().trim();
        if (categorias.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria de tarefa já cadastrada");
        }
        cat.setNome(nome);
        return categorias.save(cat);
    }

    public void excluirCategoria(Integer id) {
        TarefaCategoria cat = categorias.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        if (repository.existsByCategoriaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria vinculada a tarefas");
        }
        categorias.delete(cat);
    }

    @Transactional(readOnly = true)
    public List<Fase> listarFases() {
        return fases.findAll();
    }

    @Transactional(readOnly = true)
    public List<PrioridadeResponse> listarPrioridades() {
        return prioridadeService.listar();
    }

    public TarefaResponse criar(TarefaCadastroRequest r, Integer usuarioId) {
        Tarefa tarefa = new Tarefa();
        Fase fase = preencherBase(tarefa, r);
        tarefa.setCriadoPor(usuarioId);
        aplicarStatusAtual(tarefa, r.getStatus());
        tarefa = repository.saveAndFlush(tarefa);
        sincronizarAssociacoes(tarefa, fase, r);
        repository.flush();
        TarefaResponse response = new TarefaResponse(tarefa);
        calendario.publicarCriacao(SyncCalendarioEvent.TipoEntidade.TAREFA, tarefa.getId(), usuarioId, EventoCalendario.deTarefa(tarefa));
        return response;
    }

    public TarefaResponse atualizar(Integer id, TarefaCadastroRequest r) {
        Tarefa tarefa = obter(id);
        Fase fase = preencherBase(tarefa, r);
        if (!Objects.equals(r.getStatus(), tarefa.getStatusAtual())) {
            aplicarStatusAtual(tarefa, r.getStatus());
        }
        sincronizarAssociacoes(tarefa, fase, r);
        repository.flush();
        TarefaResponse response = new TarefaResponse(tarefa);
        calendario.publicarAtualizacao(SyncCalendarioEvent.TipoEntidade.TAREFA, id, EventoCalendario.deTarefa(tarefa));
        return response;
    }

    public TarefaResponse atualizarStatus(Integer id, TarefaStatusRequest r) {
        Tarefa tarefa = obter(id);
        if (!Objects.equals(r.getStatus(), tarefa.getStatusAtual())) {
            aplicarStatusAtual(tarefa, r.getStatus());
            repository.flush();
            calendario.publicarAtualizacao(SyncCalendarioEvent.TipoEntidade.TAREFA, id, EventoCalendario.deTarefa(tarefa));
        }
        return new TarefaResponse(tarefa);
    }

    @Transactional(readOnly = true)
    public List<StatusTarefaResponse> listarStatus(Integer tarefaId) {
        Tarefa tarefa = obter(tarefaId);
        return tarefa.getHistoricoStatus().stream().map(StatusTarefaResponse::new).toList();
    }

    public void excluir(Integer id) {
        Tarefa tarefa = obter(id);
        List<RegistroHoras> registrosDaTarefa = registroHoras.findByTarefaId(id);
        for (RegistroHoras registro : registrosDaTarefa) {
            calendario.publicarExclusao(SyncCalendarioEvent.TipoEntidade.REGISTRO_HORAS, registro.getId());
        }
        // Remove registros de horas antes das associações colaborador_tarefa (FK composta V8)
        if (!registrosDaTarefa.isEmpty()) {
            registroHoras.deleteAll(registrosDaTarefa);
            registroHoras.flush();
        }
        tarefa.getRegistrosHoras().clear();
        repository.delete(tarefa);
        calendario.publicarExclusao(SyncCalendarioEvent.TipoEntidade.TAREFA, id);
    }

    // -------- Subtarefas (V8) --------

    @Transactional(readOnly = true)
    public List<SubtarefaResponse> listarSubtarefas(Integer tarefaId) {
        obter(tarefaId);
        return subtarefas.findByTarefaIdOrderByIdAsc(tarefaId).stream().map(SubtarefaResponse::new).toList();
    }

    public SubtarefaResponse criarSubtarefa(Integer tarefaId, SubtarefaRequest request) {
        Tarefa tarefa = obter(tarefaId);
        Subtarefa sub = new Subtarefa();
        sub.setTarefa(tarefa);
        sub.setNome(request.getNome().trim());
        sub.setCheck(Boolean.TRUE.equals(request.getCheck()));
        Subtarefa salva = subtarefas.save(sub);
        sincronizarChecklistAPartirDasSubtarefas(tarefa);
        return new SubtarefaResponse(salva);
    }

    public SubtarefaResponse atualizarSubtarefa(Integer tarefaId, Integer subtarefaId, SubtarefaRequest request) {
        Tarefa tarefa = obter(tarefaId);
        Subtarefa sub = subtarefas.findByIdAndTarefaId(subtarefaId, tarefaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtarefa não encontrada"));
        sub.setNome(request.getNome().trim());
        if (request.getCheck() != null) {
            sub.setCheck(request.getCheck());
        }
        Subtarefa salva = subtarefas.save(sub);
        sincronizarChecklistAPartirDasSubtarefas(tarefa);
        return new SubtarefaResponse(salva);
    }

    public SubtarefaResponse alternarCheckSubtarefa(Integer tarefaId, Integer subtarefaId, Boolean check) {
        Tarefa tarefa = obter(tarefaId);
        Subtarefa sub = subtarefas.findByIdAndTarefaId(subtarefaId, tarefaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtarefa não encontrada"));
        sub.setCheck(check != null ? check : !sub.isCheck());
        Subtarefa salva = subtarefas.save(sub);
        sincronizarChecklistAPartirDasSubtarefas(tarefa);
        return new SubtarefaResponse(salva);
    }

    public void excluirSubtarefa(Integer tarefaId, Integer subtarefaId) {
        Tarefa tarefa = obter(tarefaId);
        Subtarefa sub = subtarefas.findByIdAndTarefaId(subtarefaId, tarefaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtarefa não encontrada"));
        tarefa.getSubtarefas().removeIf(s -> Objects.equals(s.getId(), subtarefaId));
        subtarefas.delete(sub);
        sincronizarChecklistAPartirDasSubtarefas(tarefa);
    }

    // -------- Colaboradores da tarefa (colaborador_tarefa V8) --------

    @Transactional(readOnly = true)
    public List<ColaboradorTarefaResponse> listarColaboradores(Integer tarefaId) {
        obter(tarefaId);
        return colaboradorTarefas.findByIdPkTarefa(tarefaId).stream().map(ColaboradorTarefaResponse::new).toList();
    }

    public ColaboradorTarefaResponse atribuirColaborador(Integer tarefaId, ColaboradorTarefaRequest request) {
        Tarefa tarefa = obter(tarefaId);
        Usuario colaborador = usuarios.findById(request.getColaboradorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
        ColaboradorTarefaId id = new ColaboradorTarefaId(colaborador.getId(), tarefa.getId());
        ColaboradorTarefa ct = colaboradorTarefas.findById(id).orElseGet(() -> {
            ColaboradorTarefa novo = new ColaboradorTarefa();
            novo.setColaborador(colaborador);
            novo.setTarefa(tarefa);
            return novo;
        });
        ct.setDataAtribuicao(request.getDataAtribuicao() != null ? request.getDataAtribuicao() : LocalDateTime.now());
        return new ColaboradorTarefaResponse(colaboradorTarefas.save(ct));
    }

    public void removerColaborador(Integer tarefaId, Integer colaboradorId) {
        Tarefa tarefa = obter(tarefaId);
        ColaboradorTarefaId id = new ColaboradorTarefaId(colaboradorId, tarefaId);
        ColaboradorTarefa ct = colaboradorTarefas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atribuição colaborador-tarefa não encontrada"));
        if (registroHoras.existsByColaboradorIdAndTarefaId(colaboradorId, tarefaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Colaborador possui registros de horas vinculados a esta tarefa");
        }
        tarefa.getAtribuicoes().removeIf(a -> a.getColaborador().getId().equals(colaboradorId));
        colaboradorTarefas.delete(ct);
    }

    // -------- Auxiliares internos --------

    private Tarefa obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa não encontrada"));
    }

    private StatusTarefa novoStatus(String valor) {
        StatusTarefa status = new StatusTarefa();
        status.setStatus(valor.trim());
        return status;
    }

    private void aplicarStatusAtual(Tarefa tarefa, String valor) {
        if (tarefa.getHistoricoStatus().isEmpty()) {
            tarefa.adicionarStatus(novoStatus(valor));
            return;
        }
        tarefa.getHistoricoStatus().get(0).setStatus(valor.trim());
    }

    private Fase preencherBase(Tarefa tarefa, TarefaCadastroRequest r) {
        if (r.getDataFimPrev().isBefore(r.getDataInicioPrev())
                || (r.getDataInicioReal() != null && r.getDataFimReal() != null && r.getDataFimReal().isBefore(r.getDataInicioReal()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fim anterior ao início");
        }
        Projeto projeto = projetos.findById(r.getProjetoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
        TarefaCategoria categoria = categorias.findById(r.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        Fase fase = fases.findById(r.getFkTarefaFase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fase não encontrada"));

        Prioridade prioridadeEntity = resolverPrioridade(r);

        tarefa.setProjeto(projeto);
        tarefa.setCategoria(categoria);
        tarefa.setFkTarefaFase(fase.getId());
        tarefa.setNome(r.getNome().trim());
        tarefa.setPrioridadeRef(prioridadeEntity);
        tarefa.setDataInicioPrev(r.getDataInicioPrev());
        tarefa.setDataFimPrev(r.getDataFimPrev());
        tarefa.setDataInicioReal(r.getDataInicioReal());
        tarefa.setDataFimReal(r.getDataFimReal());
        tarefa.setObservacao(r.getObservacao() == null ? null : r.getObservacao().trim());
        return fase;
    }

    private Prioridade resolverPrioridade(TarefaCadastroRequest r) {
        if (r.getPrioridadeId() != null) {
            return prioridades.findById(r.getPrioridadeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prioridade não encontrada"));
        }
        if (r.getPrioridade() == null || r.getPrioridade().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridade é obrigatória");
        }
        return prioridadeService.obterOuCriarPorNome(r.getPrioridade());
    }

    private void sincronizarAssociacoes(Tarefa tarefa, Fase fase, TarefaCadastroRequest r) {
        // 1) Colaboradores N:N (colaborador_tarefa com data_atribuicao)
        LinkedHashSet<Integer> desejados = new LinkedHashSet<>();
        if (r.getColaboradoresIds() != null) {
            for (Integer cid : r.getColaboradoresIds()) {
                if (cid != null) desejados.add(cid);
            }
        }
        if (r.getColaboradorId() != null) {
            desejados.add(r.getColaboradorId());
        }

        // Mantém vínculos que possuem registro_horas para preservar integridade referencial V8
        tarefa.getAtribuicoes().removeIf(ct -> {
            Integer cid = ct.getColaborador().getId();
            if (desejados.contains(cid)) return false;
            return !registroHoras.existsByColaboradorIdAndTarefaId(cid, tarefa.getId());
        });

        Set<Integer> existentes = new HashSet<>();
        for (ColaboradorTarefa ct : tarefa.getAtribuicoes()) {
            existentes.add(ct.getColaborador().getId());
        }
        for (Integer cid : desejados) {
            if (existentes.contains(cid)) continue;
            Usuario colab = usuarios.findById(cid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
            ColaboradorTarefa ct = new ColaboradorTarefa();
            ct.setColaborador(colab);
            ct.setTarefa(tarefa);
            ct.setDataAtribuicao(LocalDateTime.now());
            tarefa.getAtribuicoes().add(ct);
        }

        // 2) Subtarefas (V8) + Checklist (compatibilidade)
        if (r.getSubtarefas() != null) {
            tarefa.getSubtarefas().clear();
            tarefa.getChecklist().clear();
            for (SubtarefaRequest item : r.getSubtarefas()) {
                if (item.getNome() == null || item.getNome().isBlank()) continue;
                String texto = item.getNome().trim();
                boolean concluido = Boolean.TRUE.equals(item.getCheck());
                Subtarefa sub = new Subtarefa();
                sub.setTarefa(tarefa);
                sub.setNome(texto);
                sub.setCheck(concluido);
                tarefa.getSubtarefas().add(sub);

                ChecklistItem chk = new ChecklistItem();
                chk.setTarefa(tarefa);
                chk.setText(texto);
                chk.setDone(concluido);
                tarefa.getChecklist().add(chk);
            }
        } else if (r.getChecklist() != null) {
            tarefa.getChecklist().clear();
            tarefa.getSubtarefas().clear();
            for (TarefaCadastroRequest.ChecklistRequest item : r.getChecklist()) {
                String texto = item.text().trim();
                ChecklistItem checklistItem = new ChecklistItem();
                checklistItem.setTarefa(tarefa);
                checklistItem.setText(texto);
                checklistItem.setDone(item.done());
                tarefa.getChecklist().add(checklistItem);

                Subtarefa sub = new Subtarefa();
                sub.setTarefa(tarefa);
                sub.setNome(texto);
                sub.setCheck(item.done());
                tarefa.getSubtarefas().add(sub);
            }
        }

        // 3) Associação tarefa_fase (V8)
        TarefaFaseId tfId = new TarefaFaseId(tarefa.getProjeto().getId(), fase.getId(), tarefa.getId());
        boolean jaExiste = tarefa.getTarefasFases().stream().anyMatch(tf -> tfId.equals(tf.getId()));
        tarefa.getTarefasFases().removeIf(tf -> !tfId.equals(tf.getId()));
        if (!jaExiste) {
            TarefaFase tf = new TarefaFase();
            tf.setProjeto(tarefa.getProjeto());
            tf.setFase(fase);
            tf.setTarefa(tarefa);
            tarefa.getTarefasFases().add(tf);
        }
    }

    private void sincronizarChecklistAPartirDasSubtarefas(Tarefa tarefa) {
        List<Subtarefa> atuais = subtarefas.findByTarefaIdOrderByIdAsc(tarefa.getId());
        tarefa.getChecklist().clear();
        for (Subtarefa sub : atuais) {
            ChecklistItem chk = new ChecklistItem();
            chk.setTarefa(tarefa);
            chk.setText(sub.getNome());
            chk.setDone(sub.isCheck());
            tarefa.getChecklist().add(chk);
        }
        repository.flush();
    }
}
