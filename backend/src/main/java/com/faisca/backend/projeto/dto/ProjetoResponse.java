package com.faisca.backend.projeto.dto;

import com.faisca.backend.fornecedor.dto.FornecedorProjetoResponse;
import com.faisca.backend.projeto.entity.EnderecoProjeto;
import com.faisca.backend.projeto.entity.Projeto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProjetoResponse {

    @Schema(description = "ID do projeto", example = "1") private final Integer id;
    @Schema(description = "ID do cliente", example = "1") private final Integer clienteId;
    @Schema(description = "Código", example = "PRJ-001") private final String codigo;
    @Schema(description = "Relação com proprietário", example = "Proprietário") private final String relacaoProprietario;
    @Schema(description = "Nome", example = "Reforma Central") private final String nome;
    @Schema(description = "Tipo de imóvel", example = "Comercial") private final String tipoImovel;
    @Schema(description = "Área em m²", example = "120.50") private final BigDecimal areaM2;
    @Schema(description = "Unidade", example = "101") private final String unidadeNumero;
    @Schema(description = "Condomínio", example = "Edifício Central") private final String condominio;
    @Schema(description = "Matrícula", example = "12345") private final String matricula;
    @Schema(description = "CEP", example = "01310-100") private final String cep;
    @Schema(description = "Logradouro", example = "Avenida Paulista") private final String logradouro;
    @Schema(description = "Número do endereço (V8)", example = "248") private final String numero;
    @Schema(description = "Complemento", example = "Sala 101") private final String complemento;
    @Schema(description = "Bairro", example = "Bela Vista") private final String bairro;
    @Schema(description = "Cidade", example = "São Paulo") private final String cidade;
    @Schema(description = "UF", example = "SP") private final String estado;
    @Schema(description = "País", example = "Brasil") private final String pais;
    @Schema(description = "Início em horário local ISO 8601", example = "2026-11-02T09:00:00") private final LocalDateTime dataInicio;
    @Schema(description = "Fim em horário local ISO 8601", example = "2026-12-02T18:00:00") private final LocalDateTime dataFim;
    @Schema(description = "Observações", example = "Adequação do espaço") private final String observacao;
    @Schema(description = "ID da fase", example = "1") private final Integer fkTarefaFase;
    @Schema(description = "Endereço dedicado do projeto (tabela endereco_projeto V8)") private final EnderecoProjetoResponse endereco;
    @Schema(description = "Fornecedores vinculados ao projeto (tabela fornecedor_projeto V8)") private final List<FornecedorProjetoResponse> fornecedores;

    public ProjetoResponse(Projeto projeto) {
        EnderecoProjeto ep = projeto.getEnderecoProjeto();
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
        this.cep = projeto.getCep() != null ? projeto.getCep() : (ep != null ? ep.getCep() : null);
        this.logradouro = projeto.getLogradouro() != null ? projeto.getLogradouro() : (ep != null ? ep.getLogradouro() : null);
        this.numero = projeto.getNumero() != null ? projeto.getNumero() : (ep != null ? ep.getNumero() : null);
        this.complemento = projeto.getComplemento() != null ? projeto.getComplemento() : (ep != null ? ep.getComplemento() : null);
        this.bairro = projeto.getBairro() != null ? projeto.getBairro() : (ep != null ? ep.getBairro() : null);
        this.cidade = projeto.getCidade() != null ? projeto.getCidade() : (ep != null ? ep.getCidade() : null);
        this.estado = projeto.getEstado() != null ? projeto.getEstado() : (ep != null ? ep.getEstado() : null);
        this.pais = projeto.getPais() != null ? projeto.getPais() : (ep != null ? ep.getPais() : null);
        this.dataInicio = projeto.getDataInicio();
        this.dataFim = projeto.getDataFim();
        this.observacao = projeto.getObservacao();
        this.fkTarefaFase = projeto.getFkTarefaFase();
        this.endereco = ep != null ? new EnderecoProjetoResponse(ep) : null;
        this.fornecedores = projeto.getFornecedores().stream().map(FornecedorProjetoResponse::new).toList();
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
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getPais() { return pais; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public String getObservacao() { return observacao; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public EnderecoProjetoResponse getEndereco() { return endereco; }
    public List<FornecedorProjetoResponse> getFornecedores() { return fornecedores; }
}
