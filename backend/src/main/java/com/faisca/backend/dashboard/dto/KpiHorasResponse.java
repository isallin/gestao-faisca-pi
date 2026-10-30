package com.faisca.backend.dashboard.dto;

public class KpiHorasResponse {

    private Double value;
    private Integer goal;
    private Integer percent;

    public KpiHorasResponse(Double value, Integer goal, Integer percent) {
        this.value = value;
        this.goal = goal;
        this.percent = percent;
    }

    public Double getValue() {
        return value;
    }

    public Integer getGoal() {
        return goal;
    }

    public Integer getPercent() {
        return percent;
    }
}
