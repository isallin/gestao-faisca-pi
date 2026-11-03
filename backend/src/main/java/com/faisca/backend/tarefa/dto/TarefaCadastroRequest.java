package com.faisca.backend.tarefa.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;

public class TarefaCadastroRequest {

    @NotNull(message = "Projeto é obrigatório")
    @Schema(description = "ID do projeto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "ID do projeto deve ser positivo")
    private Integer projetoId;

    @NotNull(message = "Categoria é obrigatória")
    @Schema(description = "ID da categoria", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "ID da categoria deve ser positivo")
    private Integer categoriaId;

    @NotNull(message = "Fase é obrigatória")
    @Schema(description = "ID da fase", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "ID da fase deve ser positivo")
    private Integer fkTarefaFase;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome da tarefa", example = "Revisar planta", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String nome;

    @NotBlank(message = "Prioridade é obrigatória")
    @Schema(description = "Prioridade", allowableValues = {"Baixa", "Média", "Alta", "Crítica"}, example = "Alta", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "Baixa|Média|Alta|Crítica", message = "Prioridade inválida")
    private String prioridade;

    @NotNull(message = "Data de início prevista é obrigatória")
    @Schema(description = "Início previsto em horário local ISO 8601", example = "2026-11-02T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dataInicioPrev;

    @NotNull(message = "Data de fim prevista é obrigatória")
    @Schema(description = "Fim previsto em horário local ISO 8601", example = "2026-11-10T18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dataFimPrev;

    @Schema(description = "Início real opcional em horário local ISO 8601", example = "2026-11-03T09:00:00")
    private LocalDateTime dataInicioReal;

    @Schema(description = "Fim real opcional em horário local ISO 8601", example = "2026-11-09T18:00:00")
    private LocalDateTime dataFimReal;

    @Size(max = 300, message = "Observação deve ter no máximo 300 caracteres")
    @Schema(description = "Observação opcional", example = "Confirmar medidas")
    private String observacao;

    @NotBlank(message = "Status é obrigatório")
    @Schema(description = "Status livre até 70 caracteres", example = "Pendente", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 70, message = "Status deve ter no máximo 70 caracteres")
    private String status;

    @Schema(description = "ID opcional do colaborador", example = "1") private Integer colaboradorId;
    @Schema(description = "Itens opcionais do checklist") private List<@Valid ChecklistRequest> checklist;

    public record ChecklistRequest(
            @Schema(description = "Descrição do item", example = "Conferir medidas", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank @Size(max = 200) String text,
            @Schema(description = "Item concluído", example = "false") boolean done) {}
    public Integer getColaboradorId() { return colaboradorId; }
    public void setColaboradorId(Integer colaboradorId) { this.colaboradorId = colaboradorId; }
    public List<ChecklistRequest> getChecklist() { return checklist; }
    public void setChecklist(List<ChecklistRequest> checklist) { this.checklist = checklist; }

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
