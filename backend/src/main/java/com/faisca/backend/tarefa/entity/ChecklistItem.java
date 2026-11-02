package com.faisca.backend.tarefa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tarefa_checklist")
public class ChecklistItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkTarefa")
    private Tarefa tarefa;
    @Column(name = "texto", nullable = false, length = 200)
    private String text;
    @Column(name = "concluido", nullable = false)
    private boolean done;

    public Integer getId() { return id; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
