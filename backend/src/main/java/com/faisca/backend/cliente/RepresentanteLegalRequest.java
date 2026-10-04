package com.faisca.backend.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

public class RepresentanteLegalRequest {

    @NotBlank(message = "Nome do representante é obrigatório")
    @Size(max = 200)
    private String nomeCompleto;
    @CPF(message = "CPF do representante inválido")
    private String cpf;
    @Size(max = 80)
    private String cargo;
    @Email(message = "E-mail do representante inválido")
    @Size(max = 120)
    private String email;
    @Pattern(regexp = "^$|^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone do representante inválido")
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
