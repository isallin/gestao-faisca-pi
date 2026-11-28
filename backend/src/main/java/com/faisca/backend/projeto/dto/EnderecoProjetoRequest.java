package com.faisca.backend.projeto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EnderecoProjetoRequest {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    @Schema(description = "CEP", example = "01310-100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    @Schema(description = "Logradouro", example = "Avenida Paulista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    @Schema(description = "Número", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String numero;

    @NotBlank(message = "Complemento é obrigatório")
    @Size(max = 60, message = "Complemento deve ter no máximo 60 caracteres")
    @Schema(description = "Complemento", example = "Sala 101", requiredMode = Schema.RequiredMode.REQUIRED)
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 80, message = "Bairro deve ter no máximo 80 caracteres")
    @Schema(description = "Bairro", example = "Bela Vista", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 80, message = "Cidade deve ter no máximo 80 caracteres")
    @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    @Schema(description = "UF", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String estado;

    @NotBlank(message = "País é obrigatório")
    @Size(max = 80, message = "País deve ter no máximo 80 caracteres")
    @Schema(description = "País", example = "Brasil", requiredMode = Schema.RequiredMode.REQUIRED)
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
