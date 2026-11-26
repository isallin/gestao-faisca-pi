package com.faisca.backend.cliente.dto;

import com.faisca.backend.cliente.entity.RepresentanteLegal;
import io.swagger.v3.oas.annotations.media.Schema;

public record RepresentanteLegalResponse(
        @Schema(description = "ID do representante", example = "1") Integer id,
        @Schema(description = "ID do cliente PJ", example = "3") Integer clientePjId,
        @Schema(description = "Nome completo", example = "Maria Oliveira") String nomeCompleto,
        @Schema(description = "CPF", example = "52998224725") String cpf,
        @Schema(description = "Cargo", example = "Diretora") String cargo,
        @Schema(description = "E-mail", example = "maria@empresa.com") String email,
        @Schema(description = "Telefone", example = "11987654321") String telefone) {

    public RepresentanteLegalResponse(RepresentanteLegal r) {
        this(
                r.getId(),
                r.getClientePj().getId(),
                r.getNomeCompleto(),
                r.getCpf(),
                r.getCargo(),
                r.getEmail(),
                r.getTelefone()
        );
    }
}
