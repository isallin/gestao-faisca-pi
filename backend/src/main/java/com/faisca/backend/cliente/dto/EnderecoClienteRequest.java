package com.faisca.backend.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EnderecoClienteRequest {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    @Schema(description = "CEP", example = "01310-100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200)
    @Schema(description = "Logradouro", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10)
    @Schema(description = "Número", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String numero;

    @Size(max = 60)
    @Schema(description = "Complemento", example = "Sala 5")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 80)
    @Schema(description = "Bairro", example = "Bela Vista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 80)
    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    @Schema(description = "UF", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
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
