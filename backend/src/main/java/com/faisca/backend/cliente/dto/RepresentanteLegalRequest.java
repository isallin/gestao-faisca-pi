package com.faisca.backend.cliente.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

public class RepresentanteLegalRequest {

    @NotBlank(message = "Nome do representante é obrigatório")
    @Schema(description = "Nome do representante", example = "Maria Oliveira", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 200)
    private String nomeCompleto;
    @CPF(message = "CPF do representante inválido")
    @Schema(description = "CPF opcional", example = "52998224725")
    private String cpf;
    @Size(max = 80)
    @Schema(description = "Cargo do representante", example = "Diretora")
    private String cargo;
    @Email(message = "E-mail do representante inválido")
    @Schema(description = "E-mail de contato", example = "maria@empresa.com")
    @Size(max = 120)
    private String email;
    @Pattern(regexp = "^$|^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone do representante inválido")
    @Schema(description = "Telefone com DDD", example = "(11) 98765-4321")
    private String telefone;

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
