package com.faisca.backend.tarefa.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import com.faisca.backend.tarefa.entity.Tarefa;

import java.time.LocalDateTime;
import java.util.List;
import com.faisca.backend.tarefa.entity.ChecklistItem;

public class TarefaResponse {

    @Schema(description = "ID da tarefa", example = "1") private final Integer id;
    @Schema(description = "ID do projeto", example = "1") private final Integer projetoId;
    @Schema(description = "ID do cliente do projeto", example = "1") private final Integer clienteId;
    @Schema(description = "ID da categoria", example = "1") private final Integer categoriaId;
    @Schema(description = "ID da fase", example = "1") private final Integer fkTarefaFase;
    @Schema(description = "Nome", example = "Revisar planta") private final String nome;
    @Schema(description = "Prioridade", allowableValues = {"Baixa", "Média", "Alta", "Crítica"}, example = "Alta") private final String prioridade;
    @Schema(description = "Início previsto, horário local ISO 8601", example = "2026-11-02T09:00:00") private final LocalDateTime dataInicioPrev;
    @Schema(description = "Fim previsto, horário local ISO 8601", example = "2026-11-10T18:00:00") private final LocalDateTime dataFimPrev;
    @Schema(description = "Início real, horário local ISO 8601", example = "2026-11-03T09:00:00") private final LocalDateTime dataInicioReal;
    @Schema(description = "Fim real, horário local ISO 8601", example = "2026-11-09T18:00:00") private final LocalDateTime dataFimReal;
    @Schema(description = "Observação", example = "Confirmar medidas") private final String observacao;
    @Schema(description = "Status atual", example = "Pendente") private final String status;
    @Schema(description = "ID do primeiro colaborador ou nulo", example = "1") private final Integer colaboradorId;
    @Schema(description = "Itens de checklist") private final List<ChecklistResponse> checklist;

    public record ChecklistResponse(
            @Schema(description = "ID do item", example = "1") Integer id,
            @Schema(description = "Texto do item", example = "Conferir medidas") String text,
            @Schema(description = "Concluído", example = "false") boolean done) {
        public ChecklistResponse(ChecklistItem item) { this(item.getId(), item.getText(), item.isDone()); }
    }

    public TarefaResponse(Tarefa tarefa) {
        this.id = tarefa.getId();
        this.projetoId = tarefa.getProjeto().getId();
        this.clienteId = tarefa.getProjeto().getCliente().getId();
        this.categoriaId = tarefa.getCategoria().getId();
        this.fkTarefaFase = tarefa.getFkTarefaFase();
        this.nome = tarefa.getNome();
        this.prioridade = tarefa.getPrioridade();
        this.dataInicioPrev = tarefa.getDataInicioPrev();
        this.dataFimPrev = tarefa.getDataFimPrev();
        this.dataInicioReal = tarefa.getDataInicioReal();
        this.dataFimReal = tarefa.getDataFimReal();
        this.observacao = tarefa.getObservacao();
        this.status = tarefa.getStatusAtual();
        this.colaboradorId = tarefa.getColaboradores().isEmpty() ? null : tarefa.getColaboradores().get(0).getId();
        this.checklist = tarefa.getChecklist().stream().map(ChecklistResponse::new).toList();
    }

    public Integer getId() { return id; }
    public Integer getProjetoId() { return projetoId; }
    public Integer getClienteId() { return clienteId; }
    public Integer getCategoriaId() { return categoriaId; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public String getNome() { return nome; }
    public String getPrioridade() { return prioridade; }
    public LocalDateTime getDataInicioPrev() { return dataInicioPrev; }
    public LocalDateTime getDataFimPrev() { return dataFimPrev; }
    public LocalDateTime getDataInicioReal() { return dataInicioReal; }
    public LocalDateTime getDataFimReal() { return dataFimReal; }
    public String getObservacao() { return observacao; }
    public String getStatus() { return status; }
    public Integer getColaboradorId() { return colaboradorId; }
    public List<ChecklistResponse> getChecklist() { return checklist; }
}
