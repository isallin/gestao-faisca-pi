package com.faisca.backend.tarefa.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TarefaStatusRequest {
    @NotBlank
    @Schema(description = "Novo status (texto livre, até 70 caracteres)", example = "Em andamento", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 70)
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
