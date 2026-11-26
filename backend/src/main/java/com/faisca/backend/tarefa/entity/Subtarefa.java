package com.faisca.backend.tarefa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subtarefa")
public class Subtarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "`check`", nullable = false)
    private boolean check;

    public Integer getId() { return id; }
    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isCheck() { return check; }
    public void setCheck(boolean check) { this.check = check; }
}
