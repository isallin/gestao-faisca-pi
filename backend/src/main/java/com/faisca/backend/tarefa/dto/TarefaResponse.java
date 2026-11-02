package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.Tarefa;

import java.time.LocalDateTime;
import java.util.List;
import com.faisca.backend.tarefa.entity.ChecklistItem;

public class TarefaResponse {

    private final Integer id;
    private final Integer projetoId;
    private final Integer clienteId;
    private final Integer categoriaId;
    private final Integer fkTarefaFase;
    private final String nome;
    private final String prioridade;
    private final LocalDateTime dataInicioPrev;
    private final LocalDateTime dataFimPrev;
    private final LocalDateTime dataInicioReal;
    private final LocalDateTime dataFimReal;
    private final String observacao;
    private final String status;
    private final Integer colaboradorId;
    private final List<ChecklistResponse> checklist;

    public record ChecklistResponse(Integer id, String text, boolean done) {
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
