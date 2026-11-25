package com.faisca.backend.calendario.service;

import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.tarefa.entity.Prioridade;
import com.faisca.backend.tarefa.entity.StatusTarefa;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GoogleCalendarEventoServiceTests {

    private final GoogleCalendarEventoService service = new GoogleCalendarEventoService(null);

    @Test
    void eventoDeDiaUnicoGanhaFimExclusivo() {
        // O Google trata o fim de evento de dia inteiro como exclusivo (start + 1 dia)
        EventoCalendario dados = new EventoCalendario("Tarefa X", "observação",
                LocalDateTime.of(2026, 11, 20, 10, 0), LocalDateTime.of(2026, 11, 20, 12, 0), false);
        Event event = service.montarEvento(dados);

        assertEquals("Tarefa X", event.getSummary());
        assertEquals("2026-11-20", event.getStart().getDate().toString());
        assertEquals("2026-11-21", event.getEnd().getDate().toString());
    }

    @Test
    void eventoDePeriodoGanhaFimExclusivo() {
        // Período 20→22 (fim inclusivo no Faísca) precisa virar end=23 no Google,
        // porque o fim de evento de dia inteiro é exclusivo — sem isso o evento
        // aparece na agenda com um dia a menos.
        EventoCalendario dados = new EventoCalendario("Projeto A", "",
                LocalDateTime.of(2026, 11, 20, 0, 0), LocalDateTime.of(2026, 11, 22, 0, 0), false);
        Event event = service.montarEvento(dados);

        assertEquals("2026-11-20", event.getStart().getDate().toString());
        assertEquals("2026-11-23", event.getEnd().getDate().toString());
    }

    @Test
    void fimAnteriorAoInicioEhRejeitado() {
        EventoCalendario dados = new EventoCalendario("Tarefa X", "",
                LocalDateTime.of(2026, 11, 22, 0, 0), LocalDateTime.of(2026, 11, 20, 0, 0), false);

        assertThrows(IllegalArgumentException.class, () -> service.montarEvento(dados));
    }

    @Test
    void eventoHorariadoUsaHoraDeInicioEFim() {
        EventoCalendario dados = new EventoCalendario("Apontamento — Tarefa X", "reunião",
                LocalDateTime.of(2026, 11, 20, 9, 0), LocalDateTime.of(2026, 11, 20, 11, 0), true);
        Event event = service.montarEvento(dados);

        assertNotNull(event.getStart().getDateTime());
        assertNotNull(event.getEnd().getDateTime());
        assertNull(event.getStart().getDate());
    }

    @Test
    void eventoDeTarefaUsaPrioridadeRelacionadaV8() {
        Projeto projeto = new Projeto();
        projeto.setCodigo("RES-024");
        projeto.setNome("Apartamento Consolacao");
        Prioridade prioridade = new Prioridade();
        prioridade.setNome("Alta");
        StatusTarefa status = new StatusTarefa();
        status.setStatus("Em andamento");

        Tarefa tarefa = new Tarefa();
        tarefa.setNome("Revisar planta");
        tarefa.setProjeto(projeto);
        tarefa.setPrioridadeRef(prioridade);
        tarefa.adicionarStatus(status);
        tarefa.setDataInicioPrev(LocalDateTime.of(2026, 11, 20, 9, 0));
        tarefa.setDataFimPrev(LocalDateTime.of(2026, 11, 22, 18, 0));

        EventoCalendario dados = EventoCalendario.deTarefa(tarefa);

        assertTrue(dados.descricao().contains("Prioridade: Alta"), dados.descricao());
        assertTrue(dados.descricao().contains("Status: Em andamento"), dados.descricao());
    }
}
