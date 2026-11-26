package com.faisca.backend.tarefa.entity;

import com.faisca.backend.fase.entity.Fase;
import com.faisca.backend.projeto.entity.Projeto;
import jakarta.persistence.*;

@Entity
@Table(name = "tarefa_fase")
public class TarefaFase {

    @EmbeddedId
    private TarefaFaseId id = new TarefaFaseId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idProjeto")
    @JoinColumn(name = "idProjeto", nullable = false)
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idFase")
    @JoinColumn(name = "idFase", nullable = false)
    private Fase fase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("pkTarefa")
    @JoinColumn(name = "pkTarefa", nullable = false)
    private Tarefa tarefa;

    public TarefaFaseId getId() { return id; }
    public void setId(TarefaFaseId id) { this.id = id; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        if (projeto != null && projeto.getId() != null) {
            this.id.setIdProjeto(projeto.getId());
        }
    }

    public Fase getFase() { return fase; }
    public void setFase(Fase fase) {
        this.fase = fase;
        if (fase != null && fase.getId() != null) {
            this.id.setIdFase(fase.getId());
        }
    }

    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
        if (tarefa != null && tarefa.getId() != null) {
            this.id.setPkTarefa(tarefa.getId());
        }
    }
}
