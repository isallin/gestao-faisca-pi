package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.Prioridade;
import io.swagger.v3.oas.annotations.media.Schema;

public record PrioridadeResponse(
        @Schema(description = "ID da prioridade", example = "1") Integer id,
        @Schema(description = "Nome da prioridade", example = "Alta") String nome) {

    public PrioridadeResponse(Prioridade prioridade) {
        this(prioridade.getId(), prioridade.getNome());
    }
}
