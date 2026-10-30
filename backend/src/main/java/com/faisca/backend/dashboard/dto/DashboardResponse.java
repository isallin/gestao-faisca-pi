package com.faisca.backend.dashboard.dto;

import java.util.List;

public class DashboardResponse {

    private KpiTarefasResponse todayPending;
    private KpiTarefasResponse weekPending;
    private KpiTarefasResponse late;
    private KpiHorasResponse hours;
    private List<TarefaUrgenteResponse> urgentToday;
    private List<TarefasPorMesResponse> months;

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
