package com.faisca.backend.dashboard.dto;

public class KpiTarefasResponse {

    private Integer value;
    private Integer total;
    private Integer percent;

    public KpiTarefasResponse(Integer value, Integer total, Integer percent) {
        this.value = value;
        this.total = total;
        this.percent = percent;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getPercent() {
        return percent;
    }
}
