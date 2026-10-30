package com.faisca.backend.dashboard.dto;

public class TarefasPorMesResponse {

    private String key;
    private String month;
    private Integer done;
    private Integer pending;
    private Integer late;
    private Integer total;

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
