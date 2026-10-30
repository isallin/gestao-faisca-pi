package com.faisca.backend.tarefa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status")
public class StatusTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkTarefa", nullable = false)
    private Tarefa tarefa;

    @Column(name = "status", length = 70)
    private String status;

    public Integer getId() { return id; }
    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
