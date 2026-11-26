package com.faisca.backend.fornecedor.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "fornecedor_empresa")
public class FornecedorEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 150)
    private String nome;

    @Column(name = "cidade", length = 80)
    private String cidade;

    @Column(name = "origem", length = 80)
    private String origem;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "ja_trabalhei")
    private Boolean jaTrabalhei;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @ManyToMany
    @JoinTable(
            name = "fornecedor_categ_assoc",
            joinColumns = @JoinColumn(name = "pkFornecedor"),
            inverseJoinColumns = @JoinColumn(name = "pkCategoria")
    )
    @BatchSize(size = 50)
    private Set<FornecedorCategoria> categorias = new LinkedHashSet<>();

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @BatchSize(size = 50)
    private List<FornecedorContato> contatos = new ArrayList<>();

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<FornecedorProjeto> projetos = new ArrayList<>();

    public void addContato(FornecedorContato contato) {
        contato.setFornecedor(this);
        this.contatos.add(contato);
    }

    public void addProjeto(FornecedorProjeto fornecedorProjeto) {
        fornecedorProjeto.setFornecedor(this);
        this.projetos.add(fornecedorProjeto);
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Boolean getJaTrabalhei() { return jaTrabalhei; }
    public void setJaTrabalhei(Boolean jaTrabalhei) { this.jaTrabalhei = jaTrabalhei; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Set<FornecedorCategoria> getCategorias() { return categorias; }
    public List<FornecedorContato> getContatos() { return contatos; }
    public List<FornecedorProjeto> getProjetos() { return projetos; }
}
