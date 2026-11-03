package com.faisca.backend.cliente.dto;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

public class ClienteCadastroRequest {

    @NotBlank(message = "Informe o tipo de cliente")
    @Schema(description = "Tipo do cliente", allowableValues = {"PF", "PJ"}, example = "PF", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "PF|PJ", message = "Tipo deve ser PF ou PJ")
    private String tipo;
    @NotBlank(message = "E-mail é obrigatório")
    @Schema(description = "E-mail de contato", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "E-mail inválido")
    @Size(max = 120)
    private String email;
    @NotBlank(message = "Telefone é obrigatório")
    @Schema(description = "Telefone com DDD", example = "(11) 98765-4321", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;
    @Size(max = 300)
    @Schema(description = "Observação opcional", example = "Contato pela manhã")
    private String observacao;
    @Size(max = 200)
    @Schema(description = "Nome obrigatório quando tipo=PF", example = "João Silva")
    private String nome;
    @CPF(message = "CPF inválido")
    @Schema(description = "CPF obrigatório quando tipo=PF", example = "52998224725")
    private String cpf;
    @Size(max = 20)
    @Schema(description = "RG da pessoa física", example = "123456789")
    private String rg;
    @Past(message = "Data de nascimento deve estar no passado")
    @Schema(description = "Nascimento da pessoa física (AAAA-MM-DD)", example = "1990-05-21", format = "date")
    private LocalDate dataNascimento;
    @Size(max = 30)
    @Schema(description = "Estado civil da pessoa física", example = "Solteiro")
    private String estadoCivil;
    @Size(max = 80)
    @Schema(description = "Profissão da pessoa física", example = "Engenheiro")
    private String profissao;
    @CNPJ(message = "CNPJ inválido")
    @Schema(description = "CNPJ obrigatório quando tipo=PJ", example = "11222333000181")
    private String cnpj;
    @Size(max = 140)
    @Schema(description = "Nome fantasia obrigatório quando tipo=PJ", example = "Construtora Central")
    private String nomeFantasia;
    @Schema(description = "Representantes legais opcionais da pessoa jurídica")
    private List<@Valid RepresentanteLegalRequest> representantes;
    @NotBlank(message = "CEP é obrigatório")
    @Schema(description = "CEP", example = "01310-100", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;
    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Rua/avenida", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 200)
    private String logradouro;
    @NotBlank(message = "Número é obrigatório")
    @Schema(description = "Número do imóvel", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 10)
    private String numero;
    @Size(max = 60)
    @Schema(description = "Complemento opcional", example = "Sala 5")
    private String complemento;
    @NotBlank(message = "Bairro é obrigatório")
    @Schema(description = "Bairro", example = "Bela Vista", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 80)
    private String bairro;
    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 80)
    private String cidade;
    @NotBlank(message = "Estado é obrigatório")
    @Schema(description = "Sigla da UF", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    private String estado;
    @Size(max = 80)
    @Schema(description = "País opcional; padrão Brasil", example = "Brasil")
    private String pais;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public List<@Valid RepresentanteLegalRequest> getRepresentantes() {
        return representantes;
    }

    public void setRepresentantes(List<@Valid RepresentanteLegalRequest> representantes) {
        this.representantes = representantes;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
