package com.faisca.backend.projeto.dto;

import com.faisca.backend.projeto.entity.EnderecoProjeto;
import io.swagger.v3.oas.annotations.media.Schema;

public record EnderecoProjetoResponse(
        @Schema(description = "ID do endereço do projeto", example = "1") Integer id,
        @Schema(description = "ID do projeto", example = "1") Integer projetoId,
        @Schema(description = "CEP", example = "01310100") String cep,
        @Schema(description = "Logradouro", example = "Avenida Paulista") String logradouro,
        @Schema(description = "Número", example = "100") String numero,
        @Schema(description = "Complemento", example = "Sala 101") String complemento,
        @Schema(description = "Bairro", example = "Bela Vista") String bairro,
        @Schema(description = "Cidade", example = "São Paulo") String cidade,
        @Schema(description = "UF", example = "SP") String estado,
        @Schema(description = "País", example = "Brasil") String pais) {

    public EnderecoProjetoResponse(EnderecoProjeto endereco) {
        this(
                endereco.getId(),
                endereco.getProjeto().getId(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getPais()
        );
    }
}
