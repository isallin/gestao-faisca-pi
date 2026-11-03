package com.faisca.backend.dashboard.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public class KpiHorasResponse {

    @Schema(description = "Horas registradas", example = "24.5") private Double value;
    @Schema(description = "Meta de horas", example = "40") private Integer goal;
    @Schema(description = "Porcentagem da meta", example = "61") private Integer percent;

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
