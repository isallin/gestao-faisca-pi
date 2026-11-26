package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.RegistroHoras;
import com.faisca.backend.usuario.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RegistroHorasResponse(
        @Schema(description = "ID do registro", example = "1") Integer id,
        @Schema(description = "ID da tarefa", example = "12") Integer tarefaId,
        @Schema(description = "ID do colaborador vinculado (V8)", example = "1") Integer colaboradorId,
        @Schema(description = "Nome completo do colaborador vinculado", example = "Camila Ferreira") String colaboradorNome,
        @Schema(description = "Início em horário local ISO 8601", example = "2026-11-02T09:00:00") LocalDateTime inicio,
        @Schema(description = "Fim em horário local ISO 8601", example = "2026-11-02T11:30:00") LocalDateTime fim,
        @Schema(description = "Última atualização do registro (V8)", example = "2026-11-02T11:35:00") LocalDateTime ultimaAtualizacao,
        @Schema(description = "Descrição do trabalho", example = "Revisão da planta") String descricao) {

    public RegistroHorasResponse(RegistroHoras registro) {
        this(
                registro.getId(),
                registro.getTarefa().getId(),
                registro.getColaborador() != null ? registro.getColaborador().getId() : registro.getCriadoPor(),
                nomeColaborador(registro.getColaborador()),
                registro.getHoraInicio(),
                registro.getHoraFim(),
                registro.getUltimaAtualizacao(),
                registro.getDescricao()
        );
    }

    private static String nomeColaborador(Usuario u) {
        if (u == null) return null;
        String nome = u.getNome() == null ? "" : u.getNome().trim();
        String sobrenome = u.getSobrenome() == null ? "" : u.getSobrenome().trim();
        String full = (nome + " " + sobrenome).trim();
        return full.isEmpty() ? null : full;
    }
}
