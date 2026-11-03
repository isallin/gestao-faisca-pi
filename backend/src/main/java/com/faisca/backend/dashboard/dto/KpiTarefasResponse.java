package com.faisca.backend.dashboard.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public class KpiTarefasResponse {

    @Schema(description = "Quantidade no indicador", example = "4") private Integer value;
    @Schema(description = "Total considerado", example = "10") private Integer total;
    @Schema(description = "Porcentagem", example = "40") private Integer percent;

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
