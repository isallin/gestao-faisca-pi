package com.faisca.backend.tarefa.entity;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;

@Entity
@Table(name = "tarefa_categoria")
public class TarefaCategoria {

    @Id
    @Schema(description = "ID da categoria", example = "1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 80)
    @Schema(description = "Nome da categoria", example = "Projeto executivo")
    private String nome;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
