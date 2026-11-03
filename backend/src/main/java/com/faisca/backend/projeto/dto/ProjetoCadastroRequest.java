package com.faisca.backend.projeto.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProjetoCadastroRequest {

    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "ID do cliente deve ser positivo")
    private Integer clienteId;

    @NotBlank(message = "Código é obrigatório")
    @Schema(description = "Código do projeto", example = "PRJ-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 45, message = "Código deve ter no máximo 45 caracteres")
    private String codigo;

    @Size(max = 80, message = "Campo relacaoProprietario deve ter no máximo 80 caracteres")
    @Schema(description = "Relação com o proprietário", example = "Proprietário")
    private String relacaoProprietario;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do projeto", example = "Reforma Central", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String nome;

    @Size(max = 45, message = "Campo tipoImovel deve ter no máximo 45 caracteres")
    @Schema(description = "Tipo de imóvel", example = "Comercial")
    private String tipoImovel;

    @DecimalMin(value = "0.0", message = "Área não pode ser negativa")
    @Schema(description = "Área em metros quadrados", example = "120.50")
    @Digits(integer = 8, fraction = 2, message = "Área deve ter até 8 dígitos inteiros e 2 decimais")
    private BigDecimal areaM2;

    @Size(max = 20, message = "Campo unidadeNumero deve ter no máximo 20 caracteres")
    @Schema(description = "Número da unidade", example = "101")
    private String unidadeNumero;

    @Size(max = 100, message = "Campo condominio deve ter no máximo 100 caracteres")
    @Schema(description = "Condomínio", example = "Edifício Central")
    private String condominio;

    @Size(max = 50, message = "Campo matricula deve ter no máximo 50 caracteres")
    @Schema(description = "Matrícula do imóvel", example = "12345")
    private String matricula;

    @Schema(description = "Início em horário local ISO 8601", example = "2026-11-02T09:00:00")
    private LocalDateTime dataInicio;

    @Schema(description = "Fim em horário local ISO 8601", example = "2026-12-02T18:00:00")
    private LocalDateTime dataFim;

    @Size(max = 300, message = "Campo observacao deve ter no máximo 300 caracteres")
    @Schema(description = "Observações", example = "Adequação do espaço")
    private String observacao;

    @Schema(description = "CEP opcional", example = "01310-100") @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$") private String cep;
    @Schema(description = "Logradouro opcional", example = "Avenida Paulista") @Size(max = 200) private String logradouro;
    @Schema(description = "Complemento opcional", example = "Sala 101") @Size(max = 60) private String complemento;
    @Schema(description = "Bairro opcional", example = "Bela Vista") @Size(max = 80) private String bairro;
    @Schema(description = "Cidade opcional", example = "São Paulo") @Size(max = 80) private String cidade;
    @Schema(description = "UF opcional", example = "SP") @Pattern(regexp = "^$|^[A-Za-z]{2}$") private String estado;
    @Schema(description = "País opcional", example = "Brasil") @Size(max = 80) private String pais;

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
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

    @Positive(message = "ID de tarefa/fase deve ser positivo")
    @Schema(description = "ID opcional da fase", example = "1")
    private Integer fkTarefaFase;

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getRelacaoProprietario() { return relacaoProprietario; }
    public void setRelacaoProprietario(String relacaoProprietario) { this.relacaoProprietario = relacaoProprietario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoImovel() { return tipoImovel; }
    public void setTipoImovel(String tipoImovel) { this.tipoImovel = tipoImovel; }

    public BigDecimal getAreaM2() { return areaM2; }
    public void setAreaM2(BigDecimal areaM2) { this.areaM2 = areaM2; }

    public String getUnidadeNumero() { return unidadeNumero; }
    public void setUnidadeNumero(String unidadeNumero) { this.unidadeNumero = unidadeNumero; }

    public String getCondominio() { return condominio; }
    public void setCondominio(String condominio) { this.condominio = condominio; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public void setFkTarefaFase(Integer fkTarefaFase) { this.fkTarefaFase = fkTarefaFase; }

}
