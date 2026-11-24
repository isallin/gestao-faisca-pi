package com.faisca.backend.cliente.dto;

import com.faisca.backend.cliente.entity.EnderecoCliente;
import io.swagger.v3.oas.annotations.media.Schema;

public record EnderecoClienteResponse(
        @Schema(description = "ID do endereço", example = "1") Integer id,
        @Schema(description = "ID do cliente", example = "1") Integer clienteId,
        @Schema(description = "CEP", example = "01310100") String cep,
        @Schema(description = "Logradouro", example = "Avenida Paulista") String logradouro,
        @Schema(description = "Número", example = "100") String numero,
        @Schema(description = "Complemento", example = "Sala 5") String complemento,
        @Schema(description = "Bairro", example = "Bela Vista") String bairro,
        @Schema(description = "Cidade", example = "São Paulo") String cidade,
        @Schema(description = "UF", example = "SP") String estado,
        @Schema(description = "País", example = "Brasil") String pais) {

    public EnderecoClienteResponse(EnderecoCliente e) {
        this(
                e.getId(),
                e.getCliente().getId(),
                e.getCep(),
                e.getLogradouro(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getEstado(),
                e.getPais()
        );
    }
}
