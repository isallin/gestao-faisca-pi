package com.faisca.backend.fornecedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FornecedorProjetoRequest {

    @NotNull(message = "Projeto é obrigatório")
    @Positive(message = "ID do projeto deve ser positivo")
    @Schema(description = "ID do projeto vinculado", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer projetoId;

    @Size(max = 200, message = "Serviço deve ter no máximo 200 caracteres")
    @Schema(description = "Descrição do serviço prestado", example = "Marcenaria cozinha e sala")
    private String servico;

    @DecimalMin(value = "0.0", message = "Valor não pode ser negativo")
    @Digits(integer = 10, fraction = 2, message = "Valor deve ter até 10 dígitos inteiros e 2 decimais")
    @Schema(description = "Valor contratado", example = "25000.00")
    private BigDecimal valor;

    @Schema(description = "Data de início (AAAA-MM-DD)", example = "2026-11-01")
    private LocalDate dataInicio;

    @Schema(description = "Data de fim (AAAA-MM-DD)", example = "2026-12-15")
    private LocalDate dataFim;

    public Integer getProjetoId() { return projetoId; }
    public void setProjetoId(Integer projetoId) { this.projetoId = projetoId; }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
}
