package com.faisca.backend.dashboard.service;

import com.faisca.backend.dashboard.dto.*;
import com.faisca.backend.tarefa.entity.Tarefa;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.tarefa.repository.TarefaRepository;
import com.faisca.backend.usuario.entity.Usuario;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardService {
    private static final int META_HORAS_MENSAL = 160;
    private static final int MESES_NO_GRAFICO = 6;
    private static final String STATUS_CONCLUIDO = "Concluído";
    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final String[] MESES = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    private final TarefaRepository repository;

    public DashboardService(TarefaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse listar(String projeto) {
        List<Tarefa> tarefas;

        if (projeto == null || projeto.isBlank() || projeto.equals("all")) {
            tarefas = repository.findAll();
        } else {
            tarefas = repository.findByProjetoCodigo(projeto);
        }

        LocalDate hoje = LocalDate.now(FUSO);
        LocalDate inicioSemana = hoje.minusDays(hoje.getDayOfWeek().getValue() - 1);
        LocalDate fimSemana = inicioSemana.plusDays(6);
        YearMonth mesAtual = YearMonth.from(hoje);

        int totalHoje = 0;
        int pendentesHoje = 0;
        int totalSemana = 0;
        int pendentesSemana = 0;
        int emAberto = 0;
        int atrasadas = 0;
        double horasMes = 0;

        List<Tarefa> urgentes = new ArrayList<>();

        int[] concluidasPorMes = new int[MESES_NO_GRAFICO];
        int[] pendentesPorMes = new int[MESES_NO_GRAFICO];
        int[] atrasadasPorMes = new int[MESES_NO_GRAFICO];

        for (int i = 0; i < tarefas.size(); i++) {
            Tarefa t = tarefas.get(i);

            LocalDate fim = t.getDataFimPrev() == null ? null : t.getDataFimPrev().toLocalDate();
            boolean concluida = estaConcluida(t);

            if (!concluida) {
                emAberto++;
                if (fim != null && fim.isBefore(hoje)) {
                    atrasadas++;
                }
            }

            if (fim != null) {
                if (fim.equals(hoje)) {
                    totalHoje++;
                    if (!concluida) {
                        pendentesHoje++;
                        urgentes.add(t);
                    }
                }

                if (!fim.isBefore(inicioSemana) && !fim.isAfter(fimSemana)) {
                    totalSemana++;
                    if (!concluida && !fim.isBefore(hoje)) {
                        pendentesSemana++;
                    }
                }

                long distancia = ChronoUnit.MONTHS.between(YearMonth.from(fim), mesAtual);
                if (distancia >= 0 && distancia < MESES_NO_GRAFICO) {
                    int posicao = MESES_NO_GRAFICO - 1 - (int) distancia;

                    if (concluida) {
                        concluidasPorMes[posicao]++;
                    } else if (fim.isBefore(hoje)) {
                        atrasadasPorMes[posicao]++;
                    } else {
                        pendentesPorMes[posicao]++;
                    }
                }
            }

            for (RegistroHoras registro : t.getRegistrosHoras()) {
                if (registro.getHoraInicio() != null && registro.getHoraFim() != null
                        && !registro.getHoraFim().isBefore(registro.getHoraInicio())
                        && YearMonth.from(registro.getHoraInicio()).equals(mesAtual)) {
                    horasMes += Duration.between(registro.getHoraInicio(), registro.getHoraFim()).toMinutes() / 60.0;
                }
            }
        }

        urgentes.sort(Comparator.comparingInt(t -> pesoPrioridade(t.getPrioridade())));

        List<TarefaUrgenteResponse> urgentesResponse = new ArrayList<>();
        for (int i = 0; i < urgentes.size(); i++) {
            Tarefa t = urgentes.get(i);

            urgentesResponse.add(new TarefaUrgenteResponse(
                    t.getId(),
                    t.getNome(),
                    t.getProjeto() == null ? "" : t.getProjeto().getCodigo(),
                    responsaveis(t),
                    t.getPrioridade()
            ));
        }

        List<TarefasPorMesResponse> meses = new ArrayList<>();
        for (int i = 0; i < MESES_NO_GRAFICO; i++) {
            YearMonth mes = mesAtual.minusMonths(MESES_NO_GRAFICO - 1 - i);

            meses.add(new TarefasPorMesResponse(
                    mes.toString(),
                    MESES[mes.getMonthValue() - 1],
                    concluidasPorMes[i],
                    pendentesPorMes[i],
                    atrasadasPorMes[i],
                    concluidasPorMes[i] + pendentesPorMes[i] + atrasadasPorMes[i]
            ));
        }

        DashboardResponse response = new DashboardResponse(
                new KpiTarefasResponse(pendentesHoje, totalHoje, percentual(totalHoje - pendentesHoje, totalHoje)),
                new KpiTarefasResponse(pendentesSemana, totalSemana, percentual(totalSemana - pendentesSemana, totalSemana)),
                new KpiTarefasResponse(atrasadas, emAberto, percentual(atrasadas, emAberto)),
                new KpiHorasResponse(Math.round(horasMes * 10) / 10.0, META_HORAS_MENSAL, percentual(horasMes, META_HORAS_MENSAL)),
                urgentesResponse,
                meses
        );

        return response;
    }

    private boolean estaConcluida(Tarefa t) {
        return STATUS_CONCLUIDO.equalsIgnoreCase(t.getStatusAtual());
    }

    private String responsaveis(Tarefa t) {
        String nomes = "";

        for (int i = 0; i < t.getColaboradores().size(); i++) {
            if (i > 0) {
                nomes += ", ";
            }
            Usuario usuario = t.getColaboradores().get(i);
            nomes += usuario.getNome() + " " + usuario.getSobrenome();
        }

        return nomes;
    }

    private int pesoPrioridade(String prioridade) {
        if (prioridade == null) {
            return 99;
        }

        return switch (prioridade) {
            case "Crítica" -> 0;
            case "Alta" -> 1;
            case "Média" -> 2;
            case "Baixa" -> 3;
            default -> 99;
        };
    }

    private int percentual(double parte, double total) {
        if (total <= 0) {
            return 0;
        }

        return (int) Math.min(100, Math.round(parte / total * 100));
    }
}
