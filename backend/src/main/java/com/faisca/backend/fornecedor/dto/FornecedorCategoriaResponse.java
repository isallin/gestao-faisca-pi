package com.faisca.backend.fornecedor.dto;

import com.faisca.backend.fornecedor.entity.FornecedorCategoria;
import io.swagger.v3.oas.annotations.media.Schema;

public record FornecedorCategoriaResponse(
        @Schema(description = "ID da categoria", example = "1") Integer id,
        @Schema(description = "Nome da categoria", example = "Marcenaria") String nome) {

    public FornecedorCategoriaResponse(FornecedorCategoria categoria) {
        this(categoria.getId(), categoria.getNome());
    }
}
