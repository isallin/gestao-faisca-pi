package com.faisca.backend.tarefa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tarefa_categoria")
public class TarefaCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 80)
    private String nome;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
