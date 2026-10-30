package com.faisca.backend.fase.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fase")
public class Fase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfase")
    private Integer id;

    @Column(name = "nome", length = 80)
    private String nome;

    @Column(name = "ordem")
    private Integer ordem;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
