package com.faisca.backend.tarefa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TarefaStatusRequest {
    @NotBlank
    @Size(max = 70)
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
