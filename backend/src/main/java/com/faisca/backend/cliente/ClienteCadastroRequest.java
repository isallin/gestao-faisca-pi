package com.faisca.backend.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

public class ClienteCadastroRequest {

    @NotBlank(message = "Informe o tipo de cliente")
    @Pattern(regexp = "PF|PJ", message = "Tipo deve ser PF ou PJ")
    private String tipo;
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 120)
    private String email;
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;
    @Size(max = 300)
    private String observacao;
    @Size(max = 200)
    private String nome;
    @CPF(message = "CPF inválido")
    private String cpf;
    @Size(max = 20)
    private String rg;
    @Past(message = "Data de nascimento deve estar no passado")
    private LocalDate dataNascimento;
    @Size(max = 30)
    private String estadoCivil;
    @Size(max = 80)
    private String profissao;
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;
    @Size(max = 140)
    private String nomeFantasia;
    private List<@Valid RepresentanteLegalRequest> representantes;
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;
    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200)
    private String logradouro;
    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10)
    private String numero;
    @Size(max = 60)
    private String complemento;
    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 80)
    private String bairro;
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 80)
    private String cidade;
    @NotBlank(message = "Estado é obrigatório")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    private String estado;
    @Size(max = 80)
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
