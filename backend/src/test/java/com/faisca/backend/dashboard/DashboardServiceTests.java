package com.faisca.backend.dashboard;

import com.faisca.backend.dashboard.dto.DashboardResponse;
import com.faisca.backend.dashboard.service.DashboardService;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.tarefa.entity.Prioridade;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.entity.StatusTarefa;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTests {
    @Test
    void calculaHorasDoMesAPartirDoIntervaloRegistrado() {
        TarefaRepository repository = mock(TarefaRepository.class);
        Tarefa tarefa = mock(Tarefa.class);
        RegistroHoras registro = mock(RegistroHoras.class);
        RegistroHoras anterior = mock(RegistroHoras.class);
        LocalDateTime inicio = YearMonth.now(ZoneId.of("America/Sao_Paulo"))
                .atDay(1).atTime(9, 0);

        when(registro.getHoraInicio()).thenReturn(inicio);
        when(registro.getHoraFim()).thenReturn(inicio.plusHours(2).plusMinutes(30));
        when(anterior.getHoraInicio()).thenReturn(inicio.minusMonths(1));
        when(anterior.getHoraFim()).thenReturn(inicio.minusMonths(1).plusHours(4));
        when(tarefa.getRegistrosHoras()).thenReturn(List.of(registro, anterior));
        when(repository.findAll()).thenReturn(List.of(tarefa));

        DashboardResponse resposta = new DashboardService(repository).listar(null);

        assertEquals(2.5, resposta.getHours().getValue());
    }

    @Test
    void ordenaTarefasUrgentesDeHojeUsandoPrioridadeNormalizadaV8() {
        TarefaRepository repository = mock(TarefaRepository.class);
        LocalDateTime hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo")).atTime(18, 0);
        Projeto projeto = new Projeto();
        projeto.setCodigo("RES-024");

        Tarefa baixa = tarefaParaHoje("Tarefa baixa", projeto, "Baixa", hoje);
        Tarefa critica = tarefaParaHoje("Tarefa crítica", projeto, "Crítica", hoje);
        Tarefa moderada = tarefaParaHoje("Tarefa moderada", projeto, "Moderada", hoje);
        Tarefa alta = tarefaParaHoje("Tarefa alta", projeto, "Alta", hoje);

        when(repository.findAll()).thenReturn(List.of(baixa, moderada, alta, critica));

        DashboardResponse resposta = new DashboardService(repository).listar(null);

        assertEquals(4, resposta.getUrgentToday().size());
        assertEquals("Crítica", resposta.getUrgentToday().get(0).getPriority());
        assertEquals("Alta", resposta.getUrgentToday().get(1).getPriority());
        assertEquals("Moderada", resposta.getUrgentToday().get(2).getPriority());
        assertEquals("Baixa", resposta.getUrgentToday().get(3).getPriority());
    }

    @Test
    void incluiAtrasadasENovaOrdenacaoPorDataEPrioridade() {
        TarefaRepository repository = mock(TarefaRepository.class);
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        Projeto projeto = new Projeto();
        projeto.setCodigo("COM-011");

        Tarefa atrasadaCritica = tarefaParaHoje("Atrasada crítica", projeto, "Crítica", hoje.minusDays(3).atTime(18, 0));
        Tarefa atrasadaBaixa = tarefaParaHoje("Atrasada baixa", projeto, "Baixa", hoje.minusDays(3).atTime(18, 0));
        Tarefa hojeModerada = tarefaParaHoje("Hoje moderada", projeto, "Moderada", hoje.atTime(18, 0));
        Tarefa hojeMedia = tarefaParaHoje("Hoje média", projeto, "Média", hoje.atTime(18, 0));
        Tarefa futuraAlta = tarefaParaHoje("Futura alta", projeto, "Alta", hoje.plusDays(2).atTime(18, 0));

        when(repository.findAll()).thenReturn(List.of(hojeModerada, futuraAlta, atrasadaBaixa, hojeMedia, atrasadaCritica));

        DashboardResponse resposta = new DashboardService(repository).listar(null);

        assertEquals(4, resposta.getUrgentToday().size());
        assertEquals("Atrasada crítica", resposta.getUrgentToday().get(0).getName());
        assertEquals("Atrasada baixa", resposta.getUrgentToday().get(1).getName());
        assertEquals("Hoje média", resposta.getUrgentToday().get(2).getName());
        assertEquals("Hoje moderada", resposta.getUrgentToday().get(3).getName());
        assertTrue(resposta.getUrgentToday().get(0).isLate());
        assertFalse(resposta.getUrgentToday().get(2).isLate());
    }

    @Test
    void naoDuplicaTarefasUrgentesNoResultado() {
        TarefaRepository repository = mock(TarefaRepository.class);
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        Projeto projeto = new Projeto();
        projeto.setCodigo("RES-024");

        Tarefa hojeTarefa = mock(Tarefa.class);
        when(hojeTarefa.getId()).thenReturn(10);
        when(hojeTarefa.getNome()).thenReturn("Revisar planta");
        when(hojeTarefa.getProjeto()).thenReturn(projeto);
        when(hojeTarefa.getPrioridadeNome()).thenReturn("Alta");
        when(hojeTarefa.getDataFimPrev()).thenReturn(hoje.atTime(18, 0));
        when(hojeTarefa.getStatusAtual()).thenReturn("Em andamento");
        when(hojeTarefa.getColaboradores()).thenReturn(List.of());
        when(hojeTarefa.getRegistrosHoras()).thenReturn(List.of());
        when(hojeTarefa.getObservacao()).thenReturn("");

        Tarefa atrasadaTarefa = mock(Tarefa.class);
        when(atrasadaTarefa.getId()).thenReturn(20);
        when(atrasadaTarefa.getNome()).thenReturn("Enviar prancha");
        when(atrasadaTarefa.getProjeto()).thenReturn(projeto);
        when(atrasadaTarefa.getPrioridadeNome()).thenReturn("Crítica");
        when(atrasadaTarefa.getDataFimPrev()).thenReturn(hoje.minusDays(2).atTime(18, 0));
        when(atrasadaTarefa.getStatusAtual()).thenReturn("Pendente");
        when(atrasadaTarefa.getColaboradores()).thenReturn(List.of());
        when(atrasadaTarefa.getRegistrosHoras()).thenReturn(List.of());
        when(atrasadaTarefa.getObservacao()).thenReturn("Atraso confirmado");

        // Mesmo que o repositório devolvesse a mesma referência/ID mais de uma vez, não deve duplicar
        when(repository.findAll()).thenReturn(List.of(hojeTarefa, atrasadaTarefa, hojeTarefa));

        DashboardResponse resposta = new DashboardService(repository).listar(null);

        assertEquals(2, resposta.getUrgentToday().size());
        assertEquals(20, resposta.getUrgentToday().get(0).getId());
        assertEquals(10, resposta.getUrgentToday().get(1).getId());
    }

    private Tarefa tarefaParaHoje(String nome, Projeto projeto, String nomePrioridade, LocalDateTime fimPrev) {
        Prioridade p = new Prioridade();
        p.setNome(nomePrioridade);
        Tarefa t = new Tarefa();
        t.setNome(nome);
        t.setProjeto(projeto);
        t.setPrioridadeRef(p);
        t.setDataInicioPrev(fimPrev.minusHours(4));
        t.setDataFimPrev(fimPrev);
        StatusTarefa status = new StatusTarefa();
        status.setStatus("Em andamento");
        t.adicionarStatus(status);
        return t;
    }
}
