package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.ChecklistItem;
import com.faisca.backend.tarefa.entity.Subtarefa;
import com.faisca.backend.tarefa.entity.Tarefa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class TarefaResponse {

    @Schema(description = "ID da tarefa", example = "1") private final Integer id;
    @Schema(description = "ID do projeto", example = "1") private final Integer projetoId;
    @Schema(description = "ID do cliente do projeto", example = "1") private final Integer clienteId;
    @Schema(description = "ID da categoria", example = "1") private final Integer categoriaId;
    @Schema(description = "ID da fase", example = "1") private final Integer fkTarefaFase;
    @Schema(description = "Nome", example = "Revisar planta") private final String nome;
    @Schema(description = "Prioridade", allowableValues = {"Baixa", "Moderada", "Média", "Alta", "Crítica"}, example = "Alta") private final String prioridade;
    @Schema(description = "ID da prioridade na tabela prioridade (V8)", example = "4") private final Integer prioridadeId;
    @Schema(description = "Início previsto, horário local ISO 8601", example = "2026-11-02T09:00:00") private final LocalDateTime dataInicioPrev;
    @Schema(description = "Fim previsto, horário local ISO 8601", example = "2026-11-10T18:00:00") private final LocalDateTime dataFimPrev;
    @Schema(description = "Início real, horário local ISO 8601", example = "2026-11-03T09:00:00") private final LocalDateTime dataInicioReal;
    @Schema(description = "Fim real, horário local ISO 8601", example = "2026-11-09T18:00:00") private final LocalDateTime dataFimReal;
    @Schema(description = "Observação", example = "Confirmar medidas") private final String observacao;
    @Schema(description = "Status atual", example = "Pendente") private final String status;
    @Schema(description = "ID do primeiro colaborador ou nulo (compatibilidade)", example = "1") private final Integer colaboradorId;
    @Schema(description = "IDs de todos os colaboradores atribuídos (V8 N:N)") private final List<Integer> colaboradoresIds;
    @Schema(description = "Atribuições de colaboradores com data de atribuição (V8)") private final List<ColaboradorTarefaResponse> colaboradores;
    @Schema(description = "Itens de checklist (compatibilidade)") private final List<ChecklistResponse> checklist;
    @Schema(description = "Subtarefas vinculadas (V8)") private final List<SubtarefaResponse> subtarefas;

    public record ChecklistResponse(
            @Schema(description = "ID do item", example = "1") Integer id,
            @Schema(description = "Texto do item", example = "Conferir medidas") String text,
            @Schema(description = "Concluído", example = "false") boolean done) {
        public ChecklistResponse(ChecklistItem item) { this(item.getId(), item.getText(), item.isDone()); }
        public ChecklistResponse(Subtarefa sub) { this(sub.getId(), sub.getNome(), sub.isCheck()); }
    }

    public TarefaResponse(Tarefa tarefa) {
        this.id = tarefa.getId();
        this.projetoId = tarefa.getProjeto().getId();
        this.clienteId = tarefa.getProjeto().getCliente().getId();
        this.categoriaId = tarefa.getCategoria().getId();
        this.fkTarefaFase = tarefa.getFkTarefaFase();
        this.nome = tarefa.getNome();
        this.prioridade = tarefa.getPrioridadeNome();
        this.prioridadeId = tarefa.getPrioridadeRef() != null ? tarefa.getPrioridadeRef().getId() : null;
        this.dataInicioPrev = tarefa.getDataInicioPrev();
        this.dataFimPrev = tarefa.getDataFimPrev();
        this.dataInicioReal = tarefa.getDataInicioReal();
        this.dataFimReal = tarefa.getDataFimReal();
        this.observacao = tarefa.getObservacao();
        this.status = tarefa.getStatusAtual();
        this.colaboradores = tarefa.getAtribuicoes().stream().map(ColaboradorTarefaResponse::new).toList();
        this.colaboradoresIds = this.colaboradores.stream().map(ColaboradorTarefaResponse::colaboradorId).toList();
        this.colaboradorId = this.colaboradoresIds.isEmpty() ? null : this.colaboradoresIds.get(0);

        List<SubtarefaResponse> subList = tarefa.getSubtarefas().stream().map(SubtarefaResponse::new).toList();
        if (!subList.isEmpty()) {
            this.subtarefas = subList;
            this.checklist = !tarefa.getChecklist().isEmpty()
                    ? tarefa.getChecklist().stream().map(ChecklistResponse::new).toList()
                    : tarefa.getSubtarefas().stream().map(ChecklistResponse::new).toList();
        } else {
            this.checklist = tarefa.getChecklist().stream().map(ChecklistResponse::new).toList();
            this.subtarefas = tarefa.getChecklist().stream()
                    .map(c -> new SubtarefaResponse(c.getId(), tarefa.getId(), c.getText(), c.isDone()))
                    .toList();
        }
    }

    public Integer getId() { return id; }
    public Integer getProjetoId() { return projetoId; }
    public Integer getClienteId() { return clienteId; }
    public Integer getCategoriaId() { return categoriaId; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public String getNome() { return nome; }
    public String getPrioridade() { return prioridade; }
    public Integer getPrioridadeId() { return prioridadeId; }
    public LocalDateTime getDataInicioPrev() { return dataInicioPrev; }
    public LocalDateTime getDataFimPrev() { return dataFimPrev; }
    public LocalDateTime getDataInicioReal() { return dataInicioReal; }
    public LocalDateTime getDataFimReal() { return dataFimReal; }
    public String getObservacao() { return observacao; }
    public String getStatus() { return status; }
    public Integer getColaboradorId() { return colaboradorId; }
    public List<Integer> getColaboradoresIds() { return colaboradoresIds; }
    public List<ColaboradorTarefaResponse> getColaboradores() { return colaboradores; }
    public List<ChecklistResponse> getChecklist() { return checklist; }
    public List<SubtarefaResponse> getSubtarefas() { return subtarefas; }
}
