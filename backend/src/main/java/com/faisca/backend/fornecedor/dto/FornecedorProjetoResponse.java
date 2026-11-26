package com.faisca.backend.fornecedor.dto;

import com.faisca.backend.fornecedor.entity.FornecedorProjeto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FornecedorProjetoResponse(
        @Schema(description = "ID do fornecedor", example = "1") Integer fornecedorId,
        @Schema(description = "Nome do fornecedor", example = "Madeira Fina Marcenaria") String fornecedorNome,
        @Schema(description = "ID do projeto", example = "1") Integer projetoId,
        @Schema(description = "Código do projeto", example = "RES-024") String projetoCodigo,
        @Schema(description = "Nome do projeto", example = "Apartamento Consolacao") String projetoNome,
        @Schema(description = "Serviço prestado", example = "Marcenaria sob medida") String servico,
        @Schema(description = "Valor contratado", example = "25000.00") BigDecimal valor,
        @Schema(description = "Data de início", example = "2026-11-01") LocalDate dataInicio,
        @Schema(description = "Data de fim", example = "2026-12-15") LocalDate dataFim) {

    public FornecedorProjetoResponse(FornecedorProjeto fp) {
        this(
                fp.getFornecedor().getId(),
                fp.getFornecedor().getNome(),
                fp.getProjeto().getId(),
                fp.getProjeto().getCodigo(),
                fp.getProjeto().getNome(),
                fp.getServico(),
                fp.getValor(),
                fp.getDataInicio(),
                fp.getDataFim()
        );
    }
}
