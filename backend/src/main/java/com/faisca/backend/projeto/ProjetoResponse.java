package com.faisca.backend.projeto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados retornados após o cadastro de um projeto")
public class ProjetoResponse {

    @Schema(description = "ID do projeto cadastrado", example = "1")
    private final Integer id;
    @Schema(description = "ID do cliente vinculado ao projeto", example = "1")
    private final Integer clienteId;
    @Schema(description = "Código do projeto", example = "PROJ001")
    private final String codigo;
    @Schema(description = "Relação com o proprietário", example = "Proprietário")
    private final String relacaoProprietario;
    @Schema(description = "Nome do projeto", example = "Reforma de apartamento")
    private final String nome;
    @Schema(description = "Tipo do imóvel", example = "Apartamento")
    private final String tipoImovel;
    @Schema(description = "Área em metros quadrados", example = "85.50")
    private final BigDecimal areaM2;
    @Schema(description = "Número da unidade", example = "Apartamento 42")
    private final String unidadeNumero;
    @Schema(description = "Nome do condomínio", example = "Residencial Jardim")
    private final String condominio;
    @Schema(description = "Matrícula do imóvel", example = "123456")
    private final String matricula;
    @Schema(description = "Data de início", example = "2026-11-01T09:00:00")
    private final LocalDateTime dataInicio;
    @Schema(description = "Data de término", example = "2027-02-01T18:00:00")
    private final LocalDateTime dataFim;
    @Schema(description = "Observações", example = "Reforma da cozinha e sala")
    private final String observacao;
    @Schema(description = "ID da fase vinculada", example = "2")
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
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public String getObservacao() { return observacao; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
}
