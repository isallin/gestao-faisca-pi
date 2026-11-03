package com.faisca.backend.dashboard.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class DashboardResponse {

    @Schema(description = "Tarefas pendentes hoje") private KpiTarefasResponse todayPending;
    @Schema(description = "Tarefas pendentes na semana") private KpiTarefasResponse weekPending;
    @Schema(description = "Tarefas atrasadas") private KpiTarefasResponse late;
    @Schema(description = "Indicador de horas") private KpiHorasResponse hours;
    @Schema(description = "Tarefas urgentes de hoje") private List<TarefaUrgenteResponse> urgentToday;
    @Schema(description = "Tarefas agrupadas por mês") private List<TarefasPorMesResponse> months;

    public DashboardResponse(KpiTarefasResponse todayPending, KpiTarefasResponse weekPending, KpiTarefasResponse late, KpiHorasResponse hours, List<TarefaUrgenteResponse> urgentToday, List<TarefasPorMesResponse> months) {
        this.todayPending = todayPending;
        this.weekPending = weekPending;
        this.late = late;
        this.hours = hours;
        this.urgentToday = urgentToday;
        this.months = months;
    }

    public KpiTarefasResponse getTodayPending() {
        return todayPending;
    }

    public KpiTarefasResponse getWeekPending() {
        return weekPending;
    }

    public KpiTarefasResponse getLate() {
        return late;
    }

    public KpiHorasResponse getHours() {
        return hours;
    }

    public List<TarefaUrgenteResponse> getUrgentToday() {
        return urgentToday;
    }

    public List<TarefasPorMesResponse> getMonths() {
        return months;
    }
}
