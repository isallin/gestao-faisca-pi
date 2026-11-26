package com.faisca.backend.tarefa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PrioridadeRequest {

    @NotBlank(message = "Nome da prioridade é obrigatório")
    @Size(max = 45, message = "Nome da prioridade deve ter no máximo 45 caracteres")
    @Schema(description = "Nome da prioridade", example = "Alta", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
