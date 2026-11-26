package com.faisca.backend.fase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class FaseRequest {

    @NotBlank(message = "Nome da fase é obrigatório")
    @Size(max = 80, message = "Nome da fase deve ter no máximo 80 caracteres")
    @Schema(description = "Nome da fase", example = "Executivo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @PositiveOrZero(message = "Ordem não pode ser negativa")
    @Schema(description = "Ordem de exibição da fase", example = "7")
    private Integer ordem;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
