package com.faisca.backend.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
@Schema(description = "Dados necessários para cadastrar um cliente pessoa física ou jurídica")
public class ClienteCadastroRequest {

    @Schema(description = "Tipo de cliente: pessoa física ou jurídica", example = "PF", allowableValues = {"PF", "PJ"})
    @NotBlank(message = "Informe o tipo de cliente")
    @Pattern(regexp = "PF|PJ", message = "Tipo deve ser PF ou PJ")
    private String tipo;
    @Schema(description = "E-mail de contato do cliente", example = "cliente@email.com")
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 120)
    private String email;
    @Schema(description = "Telefone de contato do cliente", example = "11999999999")
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;
    @Schema(description = "Observações sobre o cliente", example = "Cliente interessado em reforma residencial")
    @Size(max = 300)
    private String observacao;
    @Schema(description = "Nome completo do cliente pessoa física", example = "João da Silva")
    @Size(max = 200)
    private String nome;
    @Schema(description = "CPF do cliente pessoa física", example = "52998224725")
    @CPF(message = "CPF inválido")
    private String cpf;
    @Schema(description = "RG do cliente pessoa física", example = "123456789")
    @Size(max = 20)
    private String rg;
    @Schema(description = "Data de nascimento do cliente pessoa física", example = "1990-05-15")
    @Past(message = "Data de nascimento deve estar no passado")
    private LocalDate dataNascimento;
    @Schema(description = "Estado civil do cliente pessoa física", example = "Casado")
    @Size(max = 30)
    private String estadoCivil;
    @Schema(description = "Profissão do cliente pessoa física", example = "Engenheiro")
    @Size(max = 80)
    private String profissao;
    @Schema(description = "CNPJ do cliente pessoa jurídica", example = "11222333000181")
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;
    @Schema(description = "Nome fantasia do cliente pessoa jurídica", example = "Arquitetura Exemplo")
    @Size(max = 140)
    private String nomeFantasia;
    @Schema(description = "Representantes legais do cliente pessoa jurídica")
    private List<@Valid RepresentanteLegalRequest> representantes;
    @Schema(description = "CEP do endereço", example = "09010000")
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;
    @Schema(description = "Logradouro do endereço", example = "Rua das Flores")
    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200)
    private String logradouro;
    @Schema(description = "Número do imóvel", example = "150")
    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10)
    private String numero;
    @Schema(description = "Complemento do endereço", example = "Sala 12")
    @Size(max = 60)
    private String complemento;
    @Schema(description = "Bairro do endereço", example = "Centro")
    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 80)
    private String bairro;
    @Schema(description = "Cidade do endereço", example = "Santo André")
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 80)
    private String cidade;
    @Schema(description = "Sigla do estado", example = "SP")
    @NotBlank(message = "Estado é obrigatório")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    private String estado;
    @Schema(description = "País do endereço", example = "Brasil")
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
