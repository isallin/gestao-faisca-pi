package com.faisca.backend.tarefa.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "prioridade")
@BatchSize(size = 50)
public class Prioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "ID da prioridade", example = "1")
    private Integer id;

    @Column(name = "nome", length = 45)
    @Schema(description = "Nome da prioridade", example = "Alta")
    private String nome;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
