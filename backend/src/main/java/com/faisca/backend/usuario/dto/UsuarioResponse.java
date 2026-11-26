package com.faisca.backend.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class UsuarioResponse {

    @Schema(description = "Identificador do usuário", example = "1") private Integer id;
    @Schema(description = "Nome", example = "Ana") private String nome;
    @Schema(description = "Sobrenome", example = "Silva") private String sobrenome;
    @Schema(description = "E-mail", example = "ana@empresa.com") private String email;
    @Schema(description = "Cargo", example = "Arquiteta") private String cargo;
    @Schema(description = "Códigos dos projetos envolvidos separados por vírgula", example = "RES-024, COM-011") private String projetos;
    @Schema(description = "Lista de IDs dos projetos envolvidos") private List<Integer> projetosIds;
    @Schema(description = "Lista de códigos dos projetos envolvidos") private List<String> projetosCodigos;
    @Schema(description = "Lista de IDs das tarefas atribuídas") private List<Integer> tarefasIds;

    public UsuarioResponse(Integer id, String nome, String sobrenome, String email, String cargo) {
        this(id, nome, sobrenome, email, cargo, "", List.of(), List.of(), List.of());
    }

    public UsuarioResponse(Integer id,
                           String nome,
                           String sobrenome,
                           String email,
                           String cargo,
                           String projetos,
                           List<Integer> projetosIds,
                           List<String> projetosCodigos,
                           List<Integer> tarefasIds) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.cargo = cargo;
        this.projetos = projetos != null ? projetos : "";
        this.projetosIds = projetosIds != null ? projetosIds : List.of();
        this.projetosCodigos = projetosCodigos != null ? projetosCodigos : List.of();
        this.tarefasIds = tarefasIds != null ? tarefasIds : List.of();
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getProjetos() { return projetos; }
    public List<Integer> getProjetosIds() { return projetosIds; }
    public List<String> getProjetosCodigos() { return projetosCodigos; }
    public List<Integer> getTarefasIds() { return tarefasIds; }
}
