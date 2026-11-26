package com.faisca.backend.fornecedor.dto;

import com.faisca.backend.fornecedor.entity.FornecedorContato;
import io.swagger.v3.oas.annotations.media.Schema;

public record FornecedorContatoResponse(
        @Schema(description = "ID do contato", example = "1") Integer id,
        @Schema(description = "ID do fornecedor", example = "1") Integer fornecedorId,
        @Schema(description = "Nome do contato", example = "Carlos Mendes") String nome,
        @Schema(description = "E-mail do contato", example = "carlos@fornecedor.com.br") String email,
        @Schema(description = "Telefone do contato", example = "(11) 98765-4321") String telefone) {

    public FornecedorContatoResponse(FornecedorContato contato) {
        this(
                contato.getId(),
                contato.getFornecedor().getId(),
                contato.getNome(),
                contato.getEmail(),
                contato.getTelefone()
        );
    }
}
