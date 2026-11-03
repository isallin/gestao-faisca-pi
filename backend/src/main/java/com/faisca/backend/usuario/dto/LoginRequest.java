package com.faisca.backend.usuario.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

public class LoginRequest {

    @NotBlank(message = "E-mail é obrigatório")
    @Schema(description = "E-mail da conta", example = "ana@empresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha da conta", format = "password", accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
