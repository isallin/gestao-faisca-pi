package com.faisca.backend.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do representante legal de um cliente pessoa jurídica")
public class RepresentanteLegalRequest {

    @Schema(description = "Nome completo do representante legal",
            example = "Maria Oliveira")
    @NotBlank(message = "Nome do representante é obrigatório")
    @Size(max = 200)
    private String nomeCompleto;
    @Schema(description = "CPF do representante legal",
            example = "52998224725")
    @CPF(message = "CPF do representante inválido")
    private String cpf;
    @Schema(description = "Cargo do representante na empresa",
            example = "Diretora")
    @Size(max = 80)
    private String cargo;
    @Schema(description = "E-mail do representante legal",
            example = "maria@empresa.com")
    @Email(message = "E-mail do representante inválido")
    @Size(max = 120)
    private String email;
    @Schema(description = "Telefone do representante legal",
            example = "11999999999")
    @Pattern(
            regexp = "^$|^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$",
            message = "Telefone do representante inválido"
    )
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
