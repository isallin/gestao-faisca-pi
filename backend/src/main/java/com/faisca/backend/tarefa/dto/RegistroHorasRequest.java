package com.faisca.backend.tarefa.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record RegistroHorasRequest(
        @Schema(description = "ID da tarefa", example = "12", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull @Positive Integer tarefaId,
        @Schema(description = "Início do trabalho, horário local ISO 8601", example = "2026-11-02T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull LocalDateTime inicio,
        @Schema(description = "Fim do trabalho, posterior ao início", example = "2026-11-02T11:30:00", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull LocalDateTime fim,
        @Schema(description = "Descrição opcional", example = "Revisão da planta") @Size(max = 300) String descricao) {}
