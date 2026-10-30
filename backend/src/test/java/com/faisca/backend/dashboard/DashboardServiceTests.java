package com.faisca.backend.dashboard;

import com.faisca.backend.dashboard.dto.DashboardResponse;
import com.faisca.backend.dashboard.service.DashboardService;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
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
        LocalDateTime inicio = YearMonth.now(java.time.ZoneId.of("America/Sao_Paulo"))
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
}
