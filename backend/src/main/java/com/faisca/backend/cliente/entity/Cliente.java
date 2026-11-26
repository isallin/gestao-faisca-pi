package com.faisca.backend.cliente.entity;

import com.faisca.backend.projeto.entity.Projeto;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo", nullable = false, length = 2)
    private String tipo;

    @Column(name = "nome", length = 150)
    private String nome;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefone", length = 11)
    private String telefone;

    @Column(name = "observacao", length = 300)
    private String observacao;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "logradouro", length = 200)
    private String logradouro;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "complemento", length = 60)
    private String complemento;

    @Column(name = "bairro", length = 80)
    private String bairro;

    @Column(name = "cidade", length = 80)
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "pais", length = 80)
    private String pais;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientePf pessoaFisica;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientePj pessoaJuridica;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    @BatchSize(size = 50)
    private List<EnderecoCliente> enderecos = new ArrayList<>();

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
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public ClientePf getPessoaFisica() { return pessoaFisica; }
    public ClientePj getPessoaJuridica() { return pessoaJuridica; }
    public List<EnderecoCliente> getEnderecos() { return enderecos; }
    public List<Projeto> getProjetos() { return projetos; }
}
