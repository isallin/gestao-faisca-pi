package com.faisca.backend.fase.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import com.faisca.backend.fase.entity.Fase;

public class FaseResponse {

    @Schema(description = "ID da fase", example = "1") private final Integer id;
    @Schema(description = "Nome da fase", example = "Planejamento") private final String nome;
    @Schema(description = "Ordem de exibição", example = "1") private final Integer ordem;

    public FaseResponse(Fase fase) {
        this.id = fase.getId();
        this.nome = fase.getNome();
        this.ordem = fase.getOrdem();
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getOrdem() {
        return ordem;
    }
}
