package com.faisca.backend.fornecedor.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "fornecedor_categoria")
public class FornecedorCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 80)
    private String nome;

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FornecedorCategoria that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
