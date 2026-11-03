package com.faisca.backend.dashboard.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public class TarefaUrgenteResponse {

    @Schema(description = "ID da tarefa", example = "1") private Integer id;
    @Schema(description = "Nome da tarefa", example = "Revisar planta") private String name;
    @Schema(description = "Projeto", example = "Reforma Central") private String project;
    @Schema(description = "Colaborador", example = "Ana Silva") private String member;
    @Schema(description = "Prioridade", example = "Alta") private String priority;

    public TarefaUrgenteResponse(Integer id, String name, String project, String member, String priority) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.member = member;
        this.priority = priority;
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
}
