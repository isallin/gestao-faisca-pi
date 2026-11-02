package com.faisca.backend.fase.dto;

import com.faisca.backend.fase.entity.Fase;

public class FaseResponse {

    private final Integer id;
    private final String nome;
    private final Integer ordem;

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
