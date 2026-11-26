package com.faisca.backend.fornecedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class FornecedorContatoRequest {

    @Size(max = 150, message = "Nome do contato deve ter no máximo 150 caracteres")
    @Schema(description = "Nome da pessoa de contato", example = "Carlos Mendes")
    private String nome;

    @Email(message = "E-mail do contato inválido")
    @Size(max = 120, message = "E-mail do contato deve ter no máximo 120 caracteres")
    @Schema(description = "E-mail do contato", example = "carlos@fornecedor.com.br")
    private String email;

    @Size(max = 20, message = "Telefone do contato deve ter no máximo 20 caracteres")
    @Schema(description = "Telefone do contato", example = "(11) 98765-4321")
    private String telefone;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
