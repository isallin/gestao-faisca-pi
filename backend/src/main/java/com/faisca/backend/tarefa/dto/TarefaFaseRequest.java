package com.faisca.backend.tarefa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TarefaFaseRequest {

    @NotNull(message = "Projeto é obrigatório")
    @Positive(message = "ID do projeto deve ser positivo")
    @Schema(description = "ID do projeto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer projetoId;

    @NotNull(message = "Fase é obrigatória")
    @Positive(message = "ID da fase deve ser positivo")
    @Schema(description = "ID da fase", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer faseId;

    @NotNull(message = "Tarefa é obrigatória")
    @Positive(message = "ID da tarefa deve ser positivo")
    @Schema(description = "ID da tarefa", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer tarefaId;

    public Integer getProjetoId() { return projetoId; }
    public void setProjetoId(Integer projetoId) { this.projetoId = projetoId; }
    public Integer getFaseId() { return faseId; }
    public void setFaseId(Integer faseId) { this.faseId = faseId; }
    public Integer getTarefaId() { return tarefaId; }
    public void setTarefaId(Integer tarefaId) { this.tarefaId = tarefaId; }
}
