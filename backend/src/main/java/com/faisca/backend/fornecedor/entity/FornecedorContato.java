package com.faisca.backend.fornecedor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedor_contato")
public class FornecedorContato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkFornecedor", nullable = false)
    private FornecedorEmpresa fornecedor;

    @Column(name = "nome", length = 150)
    private String nome;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefone", length = 15)
    private String telefone;

    public Integer getId() { return id; }
    public FornecedorEmpresa getFornecedor() { return fornecedor; }
    public void setFornecedor(FornecedorEmpresa fornecedor) { this.fornecedor = fornecedor; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
