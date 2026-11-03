package com.faisca.backend.usuario.dto;
import io.swagger.v3.oas.annotations.media.Schema;


public class UsuarioResponse {

    @Schema(description = "Identificador do usuário", example = "1") private Integer id;
    @Schema(description = "Nome", example = "Ana") private String nome;
    @Schema(description = "Sobrenome", example = "Silva") private String sobrenome;
    @Schema(description = "E-mail", example = "ana@empresa.com") private String email;
    @Schema(description = "Cargo", example = "Arquiteta") private String cargo;

    public UsuarioResponse(Integer id, String nome, String sobrenome, String email, String cargo) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.cargo = cargo;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }
}
