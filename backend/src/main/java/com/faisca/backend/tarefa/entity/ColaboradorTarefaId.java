package com.faisca.backend.tarefa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ColaboradorTarefaId implements Serializable {

    @Column(name = "pkColaborador", nullable = false)
    private Integer pkColaborador;

    @Column(name = "pkTarefa", nullable = false)
    private Integer pkTarefa;

    public ColaboradorTarefaId() {}

    public ColaboradorTarefaId(Integer pkColaborador, Integer pkTarefa) {
        this.pkColaborador = pkColaborador;
        this.pkTarefa = pkTarefa;
    }

    public Integer getPkColaborador() { return pkColaborador; }
    public void setPkColaborador(Integer pkColaborador) { this.pkColaborador = pkColaborador; }
    public Integer getPkTarefa() { return pkTarefa; }
    public void setPkTarefa(Integer pkTarefa) { this.pkTarefa = pkTarefa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColaboradorTarefaId that)) return false;
        return Objects.equals(pkColaborador, that.pkColaborador) && Objects.equals(pkTarefa, that.pkTarefa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkColaborador, pkTarefa);
    }
}
