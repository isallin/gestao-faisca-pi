package com.faisca.backend.usuario.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

public class UsuarioPatchRequest {

    @Pattern(regexp = ".*\\S.*", message = "Não pode ser vazio")
    @Schema(description = "Nome opcional", example = "Ana")
    @Size(max = 70, message = "Nome deve ter no máximo 70 caracteres")
    private String nome;
    @Pattern(regexp = ".*\\S.*", message = "Não pode ser vazio")
    @Schema(description = "Sobrenome opcional", example = "Silva")
    @Size(max = 45, message = "Sobrenome deve ter no máximo 45 caracteres")
    private String sobrenome;
    @Pattern(regexp = ".*\\S.*", message = "Não pode ser vazio")
    @Schema(description = "E-mail opcional", example = "ana@empresa.com")
    @Email(message = "E-mail inválido")
    @Size(max = 45, message = "E-mail deve ter no máximo 45 caracteres")
    private String email;
    @Pattern(regexp = ".*\\S.*", message = "Não pode ser vazio")
    @Schema(description = "Cargo opcional", example = "Arquiteta")
    @Size(max = 45, message = "Cargo deve ter no máximo 45 caracteres")
    private String cargo;

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

}
