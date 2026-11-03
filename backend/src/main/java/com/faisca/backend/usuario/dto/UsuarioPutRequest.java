package com.faisca.backend.usuario.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

public class UsuarioPutRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome", example = "Ana", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 70, message = "Nome deve ter no máximo 70 caracteres")
    private String nome;
    @NotBlank(message = "Sobrenome é obrigatório")
    @Schema(description = "Sobrenome", example = "Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 45, message = "Sobrenome deve ter no máximo 45 caracteres")
    private String sobrenome;
    @NotBlank(message = "E-mail é obrigatório")
    @Schema(description = "E-mail único", example = "ana@empresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "E-mail inválido")
    @Size(max = 45, message = "E-mail deve ter no máximo 45 caracteres")
    private String email;
    @NotBlank(message = "Cargo é obrigatório")
    @Schema(description = "Cargo", example = "Arquiteta", requiredMode = Schema.RequiredMode.REQUIRED)
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
