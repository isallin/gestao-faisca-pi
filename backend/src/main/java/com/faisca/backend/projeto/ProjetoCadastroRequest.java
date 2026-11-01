package com.faisca.backend.projeto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para cadastrar um projeto")
public class ProjetoCadastroRequest {

    @NotNull(message = "Cliente é obrigatório")
    @Positive(message = "ID do cliente deve ser positivo")
    @Schema(description = "ID do cliente vinculado ao projeto", example = "1")
    private Integer clienteId;
    @NotBlank(message = "Código é obrigatório")
    @Size(max = 45, message = "Código deve ter no máximo 45 caracteres")
    @Schema(description = "Código de identificação do projeto", example = "PROJ001")
    private String codigo;
    @Size(max = 80, message = "Campo relacaoProprietario deve ter no máximo 80 caracteres")
    @Schema(description = "Relação do cliente com o proprietário do imóvel", example = "Proprietário")
    private String relacaoProprietario;
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    @Schema(description = "Nome do projeto", example = "Reforma de apartamento")
    private String nome;
    @Size(max = 45, message = "Campo tipoImovel deve ter no máximo 45 caracteres")
    @Schema(description = "Tipo do imóvel", example = "Apartamento")
    private String tipoImovel;
    @DecimalMin(value = "0.0", message = "Área não pode ser negativa")
    @Digits(integer = 8, fraction = 2, message = "Área deve ter até 8 dígitos inteiros e 2 decimais")
    @Schema(description = "Área do imóvel em metros quadrados", example = "85.50")
    private BigDecimal areaM2;
    @Size(max = 20, message = "Campo unidadeNumero deve ter no máximo 20 caracteres")
    @Schema(description = "Número da unidade do imóvel", example = "Apartamento 42")
    private String unidadeNumero;
    @Size(max = 100, message = "Campo condominio deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do condomínio", example = "Residencial Jardim")
    private String condominio;
    @Size(max = 50, message = "Campo matricula deve ter no máximo 50 caracteres")
    @Schema(description = "Matrícula do imóvel", example = "123456")
    private String matricula;
    @Schema(description = "Data e horário de início do projeto",
            example = "2026-11-01T09:00:00")
    private LocalDateTime dataInicio;
    @Schema(description = "Data e horário de término do projeto",
            example = "2027-02-01T18:00:00")
    private LocalDateTime dataFim;
    @Size(max = 300, message = "Campo observacao deve ter no máximo 300 caracteres")
    @Schema(description = "Observações sobre o projeto",
            example = "Reforma da cozinha e sala")
    private String observacao;
    @Positive(message = "ID de tarefa/fase deve ser positivo")
    @Schema(description = "Identificador da fase vinculada ao projeto", example = "2")
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
