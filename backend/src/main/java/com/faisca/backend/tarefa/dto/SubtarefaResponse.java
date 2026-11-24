package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.Subtarefa;
import io.swagger.v3.oas.annotations.media.Schema;

public record SubtarefaResponse(
        @Schema(description = "ID da subtarefa", example = "1") Integer id,
        @Schema(description = "ID da tarefa", example = "1") Integer tarefaId,
        @Schema(description = "Nome da subtarefa", example = "Conferir medidas") String nome,
        @Schema(description = "Subtarefa concluída", example = "false") boolean check) {

    public SubtarefaResponse(Subtarefa subtarefa) {
        this(subtarefa.getId(), subtarefa.getTarefa().getId(), subtarefa.getNome(), subtarefa.isCheck());
    }
}
