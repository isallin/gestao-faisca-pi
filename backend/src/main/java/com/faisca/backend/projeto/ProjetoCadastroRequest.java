package com.faisca.backend.projeto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProjetoCadastroRequest {

    @NotNull(message = "Cliente é obrigatório")
    @Positive(message = "ID do cliente deve ser positivo")
    private Integer clienteId;

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 45, message = "Código deve ter no máximo 45 caracteres")
    private String codigo;

    @Size(max = 80, message = "Campo relacaoProprietario deve ter no máximo 80 caracteres")
    private String relacaoProprietario;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String nome;

    @Size(max = 45, message = "Campo tipoImovel deve ter no máximo 45 caracteres")
    private String tipoImovel;

    @DecimalMin(value = "0.0", message = "Área não pode ser negativa")
    @Digits(integer = 8, fraction = 2, message = "Área deve ter até 8 dígitos inteiros e 2 decimais")
    private BigDecimal areaM2;

    @Size(max = 20, message = "Campo unidadeNumero deve ter no máximo 20 caracteres")
    private String unidadeNumero;

    @Size(max = 100, message = "Campo condominio deve ter no máximo 100 caracteres")
    private String condominio;

    @Size(max = 50, message = "Campo matricula deve ter no máximo 50 caracteres")
    private String matricula;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Size(max = 300, message = "Campo observacao deve ter no máximo 300 caracteres")
    private String observacao;

    @Positive(message = "ID de tarefa/fase deve ser positivo")
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
