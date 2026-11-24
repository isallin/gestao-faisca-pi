package com.faisca.backend.tarefa.entity;

import com.faisca.backend.usuario.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "colaborador_tarefa")
public class ColaboradorTarefa {

    @EmbeddedId
    private ColaboradorTarefaId id = new ColaboradorTarefaId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("pkColaborador")
    @JoinColumn(name = "pkColaborador", nullable = false)
    private Usuario colaborador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("pkTarefa")
    @JoinColumn(name = "pkTarefa", nullable = false)
    private Tarefa tarefa;

    @Column(name = "data_atribuicao")
    private LocalDateTime dataAtribuicao;

    public ColaboradorTarefaId getId() { return id; }
    public void setId(ColaboradorTarefaId id) { this.id = id; }

    public Usuario getColaborador() { return colaborador; }
    public void setColaborador(Usuario colaborador) {
        this.colaborador = colaborador;
        if (colaborador != null && colaborador.getId() != null) {
            this.id.setPkColaborador(colaborador.getId());
        }
    }

    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
        if (tarefa != null && tarefa.getId() != null) {
            this.id.setPkTarefa(tarefa.getId());
        }
    }

    public LocalDateTime getDataAtribuicao() { return dataAtribuicao; }
    public void setDataAtribuicao(LocalDateTime dataAtribuicao) { this.dataAtribuicao = dataAtribuicao; }
}
