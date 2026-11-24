package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.ColaboradorTarefa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ColaboradorTarefaResponse(
        @Schema(description = "ID do colaborador", example = "1") Integer colaboradorId,
        @Schema(description = "Nome do colaborador", example = "Camila") String nome,
        @Schema(description = "Sobrenome do colaborador", example = "Ferreira") String sobrenome,
        @Schema(description = "E-mail do colaborador", example = "camila.ferreira@faisca.com.br") String email,
        @Schema(description = "Cargo do colaborador", example = "Arquiteta") String cargo,
        @Schema(description = "ID da tarefa", example = "1") Integer tarefaId,
        @Schema(description = "Nome da tarefa", example = "Revisar planta") String tarefaNome,
        @Schema(description = "ID do projeto da tarefa", example = "1") Integer projetoId,
        @Schema(description = "Código do projeto da tarefa", example = "RES-024") String projetoCodigo,
        @Schema(description = "Data e hora da atribuição", example = "2026-11-22T10:00:00") LocalDateTime dataAtribuicao) {

    public ColaboradorTarefaResponse(ColaboradorTarefa ct) {
        this(
                ct.getColaborador().getId(),
                ct.getColaborador().getNome(),
                ct.getColaborador().getSobrenome(),
                ct.getColaborador().getEmail(),
                ct.getColaborador().getCargo(),
                ct.getTarefa().getId(),
                ct.getTarefa().getNome(),
                ct.getTarefa().getProjeto() != null ? ct.getTarefa().getProjeto().getId() : null,
                ct.getTarefa().getProjeto() != null ? ct.getTarefa().getProjeto().getCodigo() : null,
                ct.getDataAtribuicao()
        );
    }
}
