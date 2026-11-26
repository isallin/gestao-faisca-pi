package com.faisca.backend.projeto.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco_projeto")
public class EnderecoProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idendereco_projeto")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false, unique = true)
    private Projeto projeto;

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

    public Integer getId() { return id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
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
}
