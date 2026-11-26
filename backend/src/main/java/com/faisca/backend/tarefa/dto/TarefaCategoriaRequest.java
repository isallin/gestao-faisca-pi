package com.faisca.backend.tarefa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TarefaCategoriaRequest {

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(max = 80, message = "Nome da categoria deve ter no máximo 80 caracteres")
    @Schema(description = "Nome da categoria de tarefa", example = "Projeto executivo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
