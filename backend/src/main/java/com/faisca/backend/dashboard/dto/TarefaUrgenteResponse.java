package com.faisca.backend.dashboard.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class TarefaUrgenteResponse {

    @Schema(description = "ID da tarefa", example = "1") private Integer id;
    @Schema(description = "Nome da tarefa", example = "Revisar planta") private String name;
    @Schema(description = "Projeto", example = "Reforma Central") private String project;
    @Schema(description = "Colaborador", example = "Ana Silva") private String member;
    @Schema(description = "Prioridade", example = "Alta") private String priority;
    @Schema(description = "Data de entrega", example = "2026-08-19") private LocalDate dueDate;
    @Schema(description = "Indica se a tarefa esta em atraso", example = "true") private boolean late;
    @Schema(description = "Observacao da tarefa", example = "Compatibilizar com marcenaria") private String note;

    public TarefaUrgenteResponse(Integer id, String name, String project, String member, String priority, LocalDate dueDate, boolean late, String note) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.member = member;
        this.priority = priority;
        this.dueDate = dueDate;
        this.late = late;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProject() {
        return project;
    }

    public String getMember() {
        return member;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isLate() {
        return late;
    }

    public String getNote() {
        return note;
    }
}
