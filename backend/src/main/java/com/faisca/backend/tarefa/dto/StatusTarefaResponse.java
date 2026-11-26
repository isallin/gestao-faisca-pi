package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.StatusTarefa;
import io.swagger.v3.oas.annotations.media.Schema;

public record StatusTarefaResponse(
        @Schema(description = "ID do registro de status", example = "1") Integer id,
        @Schema(description = "ID da tarefa", example = "1") Integer tarefaId,
        @Schema(description = "Descrição do status", example = "Em andamento") String status) {

    public StatusTarefaResponse(StatusTarefa statusTarefa) {
        this(statusTarefa.getId(), statusTarefa.getTarefa().getId(), statusTarefa.getStatus());
    }
}
