package com.faisca.backend.calendario.service;

import com.faisca.backend.calendario.entity.GoogleEvent;
import com.faisca.backend.calendario.repository.GoogleEventRepository;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.repository.RegistroHorasRepository;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.event.TransactionPhase;

/**
 * Sincroniza entidades do Faísca com o Google Calendar do usuário criador.
 * As operações no Google acontecem DEPOIS do commit da operação principal,
 * e qualquer falha do Google é só logada — nunca derruba a operação do Faísca.
 */
@Service
public class CalendarSyncService {

    private static final Logger log = LoggerFactory.getLogger(CalendarSyncService.class);

    private final GoogleAuthService auth;
    private final GoogleCalendarEventoService eventos;
    private final GoogleEventRepository mappings;
    private final TarefaRepository tarefas;
    private final ProjetoRepository projetos;
    private final RegistroHorasRepository registros;
    private final ApplicationEventPublisher publisher;

    public CalendarSyncService(GoogleAuthService auth, GoogleCalendarEventoService eventos,
                               GoogleEventRepository mappings, TarefaRepository tarefas,
                               ProjetoRepository projetos, RegistroHorasRepository registros,
                               ApplicationEventPublisher publisher) {
        this.auth = auth;
        this.eventos = eventos;
        this.mappings = mappings;
        this.tarefas = tarefas;
        this.projetos = projetos;
        this.registros = registros;
        this.publisher = publisher;
    }

    // -------- publicação (chamada pelos services dentro da transação) --------

    /** Entidade nova: cria o evento no calendário do usuário informado. */
    public void publicarCriacao(SyncCalendarioEvent.TipoEntidade tipo, Integer origemId,
                                Integer usuarioId, EventoCalendario dados) {
        publisher.publishEvent(new SyncCalendarioEvent(tipo, origemId, usuarioId, SyncCalendarioEvent.Acao.CRIAR, dados));
    }

    /** Entidade editada: atualiza o evento em todos os calendários que já têm o mapeamento. */
    public void publicarAtualizacao(SyncCalendarioEvent.TipoEntidade tipo, Integer origemId, EventoCalendario dados) {
        publisher.publishEvent(new SyncCalendarioEvent(tipo, origemId, null, SyncCalendarioEvent.Acao.ATUALIZAR, dados));
    }

    /** Entidade excluída: apaga os eventos correspondentes. */
    public void publicarExclusao(SyncCalendarioEvent.TipoEntidade tipo, Integer origemId) {
        publisher.publishEvent(new SyncCalendarioEvent(tipo, origemId, null, SyncCalendarioEvent.Acao.EXCLUIR, null));
    }

    // -------- execução (depois do commit) --------

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Async("calendarSyncExecutor")
    public void aoSincronizar(SyncCalendarioEvent evento) {
        try {
            switch (evento.acao()) {
                case CRIAR -> criarEvento(evento.tipo(), evento.origemId(), evento.usuarioId(), evento.dados());
                case ATUALIZAR -> atualizarEventos(evento.tipo(), evento.origemId(), evento.dados());
                case EXCLUIR -> removerEventos(evento.tipo(), evento.origemId());
            }
        } catch (Exception e) {
            log.warn("Falha ao sincronizar {} {} com o Google Calendar: {}",
                    evento.tipo(), evento.origemId(), e.getMessage());
        }
    }

    /**
     * Ao conectar o Google, cria eventos para as entidades do próprio usuário
     * que ainda não têm evento (ex.: criadas antes da conexão ou com o Google desconectado).
     */
    @Transactional
    @Async("calendarSyncExecutor")
    public void sincronizarPendentes(Integer usuarioId) {
        if (!auth.isConectado(usuarioId)) return;

        for (Tarefa t : tarefas.findByCriadoPor(usuarioId)) {
            criarEvento(SyncCalendarioEvent.TipoEntidade.TAREFA, t.getId(), usuarioId, EventoCalendario.deTarefa(t));
        }
        for (Projeto p : projetos.findByCriadoPor(usuarioId)) {
            EventoCalendario.deProjeto(p).ifPresent(dados ->
                    criarEvento(SyncCalendarioEvent.TipoEntidade.PROJETO, p.getId(), usuarioId, dados));
        }
        for (RegistroHoras r : registros.findByCriadoPor(usuarioId)) {
            criarEvento(SyncCalendarioEvent.TipoEntidade.REGISTRO_HORAS, r.getId(), usuarioId, EventoCalendario.deRegistro(r));
        }
    }

    // -------- internos --------

    private void criarEvento(SyncCalendarioEvent.TipoEntidade tipo, Integer origemId,
                             Integer usuarioId, EventoCalendario dados) {
        if (dados == null || usuarioId == null || !auth.isConectado(usuarioId)) return;
        if (mappings.existsByTipoAndFkOrigemAndFkUsuario(tipo.name(), origemId, usuarioId)) return;
        try {
            String googleEventId = eventos.criar(usuarioId, dados);
            mappings.save(new GoogleEvent(tipo.name(), origemId, usuarioId, googleEventId));
        } catch (Exception e) {
            log.warn("Falha ao criar evento do Google para {} {}: {}", tipo, origemId, e.getMessage());
        }
    }

    private void atualizarEventos(SyncCalendarioEvent.TipoEntidade tipo, Integer origemId, EventoCalendario dados) {
        if (dados == null) return;
        for (GoogleEvent mapping : mappings.findByTipoAndFkOrigem(tipo.name(), origemId)) {
            try {
                if (!auth.isConectado(mapping.getFkUsuario())) continue;
                String novoId = eventos.atualizar(mapping.getFkUsuario(), mapping.getGoogleEventId(), dados);
                if (!novoId.equals(mapping.getGoogleEventId())) {
                    mapping.setGoogleEventId(novoId);
                    mappings.save(mapping);
                }
            } catch (Exception e) {
                log.warn("Falha ao atualizar evento do Google {} de {} {}: {}",
                        mapping.getGoogleEventId(), tipo, origemId, e.getMessage());
            }
        }
    }

    private void removerEventos(SyncCalendarioEvent.TipoEntidade tipo, Integer origemId) {
        for (GoogleEvent mapping : mappings.findByTipoAndFkOrigem(tipo.name(), origemId)) {
            try {
                if (auth.isConectado(mapping.getFkUsuario())) {
                    eventos.remover(mapping.getFkUsuario(), mapping.getGoogleEventId());
                }
            } catch (Exception e) {
                log.warn("Falha ao remover evento do Google {} de {} {}: {}",
                        mapping.getGoogleEventId(), tipo, origemId, e.getMessage());
            }
            mappings.delete(mapping);
        }
    }
}
