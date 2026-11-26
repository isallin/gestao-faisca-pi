package com.faisca.backend.tarefa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubtarefaRequest {

    @NotBlank(message = "Nome da subtarefa é obrigatório")
    @Size(max = 200, message = "Nome da subtarefa deve ter no máximo 200 caracteres")
    @Schema(description = "Nome/descrição da subtarefa", example = "Conferir medidas", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Schema(description = "Indica se a subtarefa está concluída", example = "false")
    private Boolean check;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Boolean getCheck() { return check; }
    public void setCheck(Boolean check) { this.check = check; }
}
