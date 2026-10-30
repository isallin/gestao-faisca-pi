package com.faisca.backend.dashboard.dto;

public class TarefaUrgenteResponse {

    private Integer id;
    private String name;
    private String project;
    private String member;
    private String priority;

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
