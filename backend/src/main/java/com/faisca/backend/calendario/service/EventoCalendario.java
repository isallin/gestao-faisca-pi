package com.faisca.backend.calendario.service;

import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.entity.Tarefa;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Dados prontos de um evento do Google Calendar.
 * {@code horariado} = true usa hora de início/fim (apontamentos de hora);
 * false cria evento de dia inteiro (tarefas e projetos).
 */
public record EventoCalendario(String summary, String descricao,
                               LocalDateTime inicio, LocalDateTime fim, boolean horariado) {

    public static EventoCalendario deTarefa(Tarefa t) {
        String descricao = t.getObservacao() == null ? "" : t.getObservacao();
        String prioridade = t.getPrioridadeNome() == null ? "" : t.getPrioridadeNome();
        String contexto = "Projeto: " + t.getProjeto().getCodigo() + " — " + t.getProjeto().getNome()
                + "\nPrioridade: " + prioridade
                + "\nStatus: " + t.getStatusAtual();
        return new EventoCalendario(t.getNome(), descricao.isBlank() ? contexto : descricao + "\n\n" + contexto,
                t.getDataInicioPrev(), t.getDataFimPrev(), false);
    }

    /** Projeto sem datas não gera evento. */
    public static Optional<EventoCalendario> deProjeto(Projeto p) {
        if (p.getDataInicio() == null && p.getDataFim() == null) return Optional.empty();
        LocalDateTime inicio = p.getDataInicio() != null ? p.getDataInicio() : p.getDataFim();
        LocalDateTime fim = p.getDataFim() != null ? p.getDataFim() : inicio;
        return Optional.of(new EventoCalendario(p.getCodigo() + " — " + p.getNome(),
                p.getObservacao(), inicio, fim, false));
    }

    public static EventoCalendario deRegistro(RegistroHoras r) {
        return new EventoCalendario("Apontamento — " + r.getTarefa().getNome(),
                r.getDescricao(), r.getHoraInicio(), r.getHoraFim(), true);
    }
}
