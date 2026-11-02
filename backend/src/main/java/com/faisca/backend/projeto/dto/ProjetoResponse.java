package com.faisca.backend.projeto.dto;

import com.faisca.backend.projeto.entity.Projeto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProjetoResponse {

    private final Integer id;
    private final Integer clienteId;
    private final String codigo;
    private final String relacaoProprietario;
    private final String nome;
    private final String tipoImovel;
    private final BigDecimal areaM2;
    private final String unidadeNumero;
    private final String condominio;
    private final String matricula;
    private final String cep, logradouro, complemento, bairro, cidade, estado, pais;
    private final LocalDateTime dataInicio;
    private final LocalDateTime dataFim;
    private final String observacao;
    private final Integer fkTarefaFase;

    public ProjetoResponse(Projeto projeto) {
        this.id = projeto.getId();
        this.clienteId = projeto.getCliente().getId();
        this.codigo = projeto.getCodigo();
        this.relacaoProprietario = projeto.getRelacaoProprietario();
        this.nome = projeto.getNome();
        this.tipoImovel = projeto.getTipoImovel();
        this.areaM2 = projeto.getAreaM2();
        this.unidadeNumero = projeto.getUnidadeNumero();
        this.condominio = projeto.getCondominio();
        this.matricula = projeto.getMatricula();
        this.cep = projeto.getCep(); this.logradouro = projeto.getLogradouro();
        this.complemento = projeto.getComplemento(); this.bairro = projeto.getBairro();
        this.cidade = projeto.getCidade(); this.estado = projeto.getEstado(); this.pais = projeto.getPais();
        this.dataInicio = projeto.getDataInicio();
        this.dataFim = projeto.getDataFim();
        this.observacao = projeto.getObservacao();
        this.fkTarefaFase = projeto.getFkTarefaFase();
    }

    public Integer getId() { return id; }
    public Integer getClienteId() { return clienteId; }
    public String getCodigo() { return codigo; }
    public String getRelacaoProprietario() { return relacaoProprietario; }
    public String getNome() { return nome; }
    public String getTipoImovel() { return tipoImovel; }
    public BigDecimal getAreaM2() { return areaM2; }
    public String getUnidadeNumero() { return unidadeNumero; }
    public String getCondominio() { return condominio; }
    public String getMatricula() { return matricula; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getPais() { return pais; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public String getObservacao() { return observacao; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
}
