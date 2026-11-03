package com.faisca.backend.dashboard.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public class TarefasPorMesResponse {

    @Schema(description = "Chave do mês", example = "2026-11") private String key;
    @Schema(description = "Nome abreviado do mês", example = "nov") private String month;
    @Schema(description = "Concluídas", example = "3") private Integer done;
    @Schema(description = "Pendentes", example = "4") private Integer pending;
    @Schema(description = "Atrasadas", example = "1") private Integer late;
    @Schema(description = "Total", example = "8") private Integer total;

    public TarefasPorMesResponse(String key, String month, Integer done, Integer pending, Integer late, Integer total) {
        this.key = key;
        this.month = month;
        this.done = done;
        this.pending = pending;
        this.late = late;
        this.total = total;
    }

    public String getKey() {
        return key;
    }

    public String getMonth() {
        return month;
    }

    public Integer getDone() {
        return done;
    }

    public Integer getPending() {
        return pending;
    }

    public Integer getLate() {
        return late;
    }

    public Integer getTotal() {
        return total;
    }
}
