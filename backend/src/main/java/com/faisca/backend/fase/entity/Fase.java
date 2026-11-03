package com.faisca.backend.fase.entity;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;

@Entity
@Table(name = "fase")
public class Fase {

    @Id
    @Schema(description = "ID da fase", example = "1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfase")
    private Integer id;

    @Column(name = "nome", length = 80)
    @Schema(description = "Nome da fase", example = "Planejamento")
    private String nome;

    @Column(name = "ordem")
    @Schema(description = "Ordem de exibição", example = "1")
    private Integer ordem;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
