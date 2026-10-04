package com.faisca.backend.cliente;

import java.util.ArrayList;
import java.util.List;

import com.faisca.backend.projeto.Projeto;
import org.hibernate.annotations.BatchSize;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo", nullable = false, length = 2)
    private String tipo;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefone", length = 11)
    private String telefone;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientePf pessoaFisica;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientePj pessoaJuridica;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    @BatchSize(size = 50)
    private List<EnderecoCliente> enderecos = new ArrayList<>();

    /** Somente leitura: projetos pertencem ao módulo de projetos. */
    @OneToMany(mappedBy = "cliente")
    @OrderBy("codigo")
    @BatchSize(size = 50)
    private List<Projeto> projetos = new ArrayList<>();

    public void setPessoaFisica(ClientePf pf) {
        this.pessoaFisica = pf;
        pf.setCliente(this);
    }

    public void setPessoaJuridica(ClientePj pj) {
        this.pessoaJuridica = pj;
        pj.setCliente(this);
    }

    public void addEndereco(EnderecoCliente endereco) {
        endereco.setCliente(this);
        this.enderecos.add(endereco);
    }

    public Integer getId() { return id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public ClientePf getPessoaFisica() { return pessoaFisica; }
    public ClientePj getPessoaJuridica() { return pessoaJuridica; }
    public List<EnderecoCliente> getEnderecos() { return enderecos; }
    public List<Projeto> getProjetos() { return projetos; }
}
