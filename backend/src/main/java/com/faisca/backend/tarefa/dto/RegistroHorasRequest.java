package com.faisca.backend.tarefa.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record RegistroHorasRequest(
        @NotNull @Positive Integer tarefaId,
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim,
        @Size(max = 300) String descricao) {}
