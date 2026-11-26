package com.faisca.backend.projeto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EnderecoProjetoRequest {

    @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$", message = "CEP inválido")
    @Schema(description = "CEP", example = "01310-100")
    private String cep;

    @Size(max = 200)
    @Schema(description = "Logradouro", example = "Avenida Paulista")
    private String logradouro;

    @Size(max = 10)
    @Schema(description = "Número", example = "100")
    private String numero;

    @Size(max = 60)
    @Schema(description = "Complemento", example = "Sala 101")
    private String complemento;

    @Size(max = 80)
    @Schema(description = "Bairro", example = "Bela Vista")
    private String bairro;

    @Size(max = 80)
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Pattern(regexp = "^$|^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    @Schema(description = "UF", example = "SP")
    private String estado;

    @Size(max = 80)
    @Schema(description = "País", example = "Brasil")
    private String pais;

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
