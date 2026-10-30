package com.faisca.backend.tarefa.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class TarefaCadastroRequest {

    @NotNull(message = "Projeto é obrigatório")
    @Positive(message = "ID do projeto deve ser positivo")
    private Integer projetoId;

    @NotNull(message = "Categoria é obrigatória")
    @Positive(message = "ID da categoria deve ser positivo")
    private Integer categoriaId;

    @NotNull(message = "Fase é obrigatória")
    @Positive(message = "ID da fase deve ser positivo")
    private Integer fkTarefaFase;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String nome;

    @NotBlank(message = "Prioridade é obrigatória")
    @Pattern(regexp = "Baixa|Média|Alta|Crítica", message = "Prioridade inválida")
    private String prioridade;

    @NotNull(message = "Data de início prevista é obrigatória")
    private LocalDateTime dataInicioPrev;

    @NotNull(message = "Data de fim prevista é obrigatória")
    private LocalDateTime dataFimPrev;

    private LocalDateTime dataInicioReal;

    private LocalDateTime dataFimReal;

    @Size(max = 300, message = "Observação deve ter no máximo 300 caracteres")
    private String observacao;

    @NotBlank(message = "Status é obrigatório")
    @Size(max = 70, message = "Status deve ter no máximo 70 caracteres")
    private String status;

    public Integer getProjetoId() { return projetoId; }
    public void setProjetoId(Integer projetoId) { this.projetoId = projetoId; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public void setFkTarefaFase(Integer fkTarefaFase) { this.fkTarefaFase = fkTarefaFase; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataInicioPrev() { return dataInicioPrev; }
    public void setDataInicioPrev(LocalDateTime dataInicioPrev) { this.dataInicioPrev = dataInicioPrev; }
    public LocalDateTime getDataFimPrev() { return dataFimPrev; }
    public void setDataFimPrev(LocalDateTime dataFimPrev) { this.dataFimPrev = dataFimPrev; }
    public LocalDateTime getDataInicioReal() { return dataInicioReal; }
    public void setDataInicioReal(LocalDateTime dataInicioReal) { this.dataInicioReal = dataInicioReal; }
    public LocalDateTime getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDateTime dataFimReal) { this.dataFimReal = dataFimReal; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
