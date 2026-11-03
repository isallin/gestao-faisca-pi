package com.faisca.backend.tarefa.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import com.faisca.backend.tarefa.entity.RegistroHoras;
import java.time.LocalDateTime;

public record RegistroHorasResponse(
        @Schema(description = "ID do registro", example = "1") Integer id,
        @Schema(description = "ID da tarefa", example = "12") Integer tarefaId,
        @Schema(description = "Início em horário local ISO 8601", example = "2026-11-02T09:00:00") LocalDateTime inicio,
        @Schema(description = "Fim em horário local ISO 8601", example = "2026-11-02T11:30:00") LocalDateTime fim,
        @Schema(description = "Descrição do trabalho", example = "Revisão da planta") String descricao) {
    public RegistroHorasResponse(RegistroHoras registro) {
        this(registro.getId(), registro.getTarefa().getId(), registro.getHoraInicio(), registro.getHoraFim(), registro.getDescricao());
    }
}
