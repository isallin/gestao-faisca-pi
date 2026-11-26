package com.faisca.backend.tarefa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class ColaboradorTarefaRequest {

    @NotNull(message = "Colaborador é obrigatório")
    @Positive(message = "ID do colaborador deve ser positivo")
    @Schema(description = "ID do colaborador", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer colaboradorId;

    @Schema(description = "Data e hora da atribuição (opcional; padrão agora)", example = "2026-11-22T10:00:00")
    private LocalDateTime dataAtribuicao;

    public Integer getColaboradorId() { return colaboradorId; }
    public void setColaboradorId(Integer colaboradorId) { this.colaboradorId = colaboradorId; }
    public LocalDateTime getDataAtribuicao() { return dataAtribuicao; }
    public void setDataAtribuicao(LocalDateTime dataAtribuicao) { this.dataAtribuicao = dataAtribuicao; }
}
