package com.faisca.backend.tarefa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TarefaFaseId implements Serializable {

    @Column(name = "idProjeto", nullable = false)
    private Integer idProjeto;

    @Column(name = "idFase", nullable = false)
    private Integer idFase;

    @Column(name = "pkTarefa", nullable = false)
    private Integer pkTarefa;

    public TarefaFaseId() {}

    public TarefaFaseId(Integer idProjeto, Integer idFase, Integer pkTarefa) {
        this.idProjeto = idProjeto;
        this.idFase = idFase;
        this.pkTarefa = pkTarefa;
    }

    public Integer getIdProjeto() { return idProjeto; }
    public void setIdProjeto(Integer idProjeto) { this.idProjeto = idProjeto; }
    public Integer getIdFase() { return idFase; }
    public void setIdFase(Integer idFase) { this.idFase = idFase; }
    public Integer getPkTarefa() { return pkTarefa; }
    public void setPkTarefa(Integer pkTarefa) { this.pkTarefa = pkTarefa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TarefaFaseId that)) return false;
        return Objects.equals(idProjeto, that.idProjeto)
                && Objects.equals(idFase, that.idFase)
                && Objects.equals(pkTarefa, that.pkTarefa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProjeto, idFase, pkTarefa);
    }
}
