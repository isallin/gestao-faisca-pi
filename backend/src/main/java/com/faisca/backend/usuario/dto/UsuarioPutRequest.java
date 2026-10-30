package com.faisca.backend.usuario.dto;

import jakarta.validation.constraints.*;

public class UsuarioPutRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 70, message = "Nome deve ter no máximo 70 caracteres")
    private String nome;
    @NotBlank(message = "Sobrenome é obrigatório")
    @Size(max = 45, message = "Sobrenome deve ter no máximo 45 caracteres")
    private String sobrenome;
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 45, message = "E-mail deve ter no máximo 45 caracteres")
    private String email;
    @NotBlank(message = "Cargo é obrigatório")
    @Size(max = 45, message = "Cargo deve ter no máximo 45 caracteres")
    private String cargo;
    @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
