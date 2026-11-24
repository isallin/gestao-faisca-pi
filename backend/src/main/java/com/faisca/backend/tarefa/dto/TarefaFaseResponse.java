package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.TarefaFase;
import io.swagger.v3.oas.annotations.media.Schema;

public record TarefaFaseResponse(
        @Schema(description = "ID do projeto", example = "1") Integer projetoId,
        @Schema(description = "Código do projeto", example = "RES-024") String projetoCodigo,
        @Schema(description = "ID da fase", example = "7") Integer faseId,
        @Schema(description = "Nome da fase", example = "Executivo") String faseNome,
        @Schema(description = "ID da tarefa", example = "1") Integer tarefaId,
        @Schema(description = "Nome da tarefa", example = "Revisar planta") String tarefaNome) {

    public TarefaFaseResponse(TarefaFase tf) {
        this(
                tf.getProjeto().getId(),
                tf.getProjeto().getCodigo(),
                tf.getFase().getId(),
                tf.getFase().getNome(),
                tf.getTarefa().getId(),
                tf.getTarefa().getNome()
        );
    }
}
