package com.faisca.backend.fornecedor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FornecedorProjetoId implements Serializable {

    @Column(name = "fornecedor_id", nullable = false)
    private Integer fornecedorId;

    @Column(name = "projeto_id", nullable = false)
    private Integer projetoId;

    public FornecedorProjetoId() {}

    public FornecedorProjetoId(Integer fornecedorId, Integer projetoId) {
        this.fornecedorId = fornecedorId;
        this.projetoId = projetoId;
    }

    public Integer getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(Integer fornecedorId) { this.fornecedorId = fornecedorId; }
    public Integer getProjetoId() { return projetoId; }
    public void setProjetoId(Integer projetoId) { this.projetoId = projetoId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FornecedorProjetoId that)) return false;
        return Objects.equals(fornecedorId, that.fornecedorId) && Objects.equals(projetoId, that.projetoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fornecedorId, projetoId);
    }
}
