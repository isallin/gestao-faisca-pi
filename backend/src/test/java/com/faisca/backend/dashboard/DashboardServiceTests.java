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
