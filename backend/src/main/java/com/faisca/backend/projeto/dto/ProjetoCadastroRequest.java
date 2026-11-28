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

    @NotBlank(message = "Tipo de imóvel é obrigatório")
    @Size(max = 45, message = "Tipo de imóvel deve ter no máximo 45 caracteres")
    @Schema(description = "Tipo de imóvel", example = "Comercial", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tipoImovel;

    @NotNull(message = "Metragem é obrigatória")
    @DecimalMin(value = "0.0", message = "Área não pode ser negativa")
    @Schema(description = "Área em metros quadrados", example = "120.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @Digits(integer = 8, fraction = 2, message = "Área deve ter até 8 dígitos inteiros e 2 decimais")
    private BigDecimal areaM2;

    @Size(max = 20, message = "Campo unidadeNumero deve ter no máximo 20 caracteres")
    @Schema(description = "Número da unidade", example = "101")
    private String unidadeNumero;

    @Size(max = 100, message = "Condomínio deve ter no máximo 100 caracteres")
    @Schema(description = "Condomínio opcional", example = "Edifício Central")
    private String condominio;

    @Size(max = 50, message = "Matrícula deve ter no máximo 50 caracteres")
    @Schema(description = "Matrícula do imóvel opcional", example = "12345")
    private String matricula;

    @NotNull(message = "Data de início é obrigatória")
    @Schema(description = "Início em horário local ISO 8601", example = "2026-11-02T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dataInicio;

    @NotNull(message = "Data do fim é obrigatória")
    @Schema(description = "Fim em horário local ISO 8601", example = "2026-12-02T18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dataFim;

    @Size(max = 300, message = "Observação deve ter no máximo 300 caracteres")
    @Schema(description = "Observações opcionais", example = "Adequação do espaço")
    private String observacao;

    @NotBlank(message = "CEP é obrigatório")
    @Schema(description = "CEP", example = "01310-100", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Logradouro", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Schema(description = "Número do endereço do imóvel (V8)", example = "248", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    private String numero;

    @NotBlank(message = "Complemento é obrigatório")
    @Schema(description = "Complemento", example = "Sala 101", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 60, message = "Complemento deve ter no máximo 60 caracteres")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Schema(description = "Bairro", example = "Bela Vista", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 80, message = "Bairro deve ter no máximo 80 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 80, message = "Cidade deve ter no máximo 80 caracteres")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Schema(description = "Sigla da UF", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    private String estado;

    @NotBlank(message = "País é obrigatório")
    @Schema(description = "País", example = "Brasil", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 80, message = "País deve ter no máximo 80 caracteres")
    private String pais;

    @NotNull(message = "Fase é obrigatória")
    @Positive(message = "ID de tarefa/fase deve ser positivo")
    @Schema(description = "ID da fase", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fkTarefaFase;

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
