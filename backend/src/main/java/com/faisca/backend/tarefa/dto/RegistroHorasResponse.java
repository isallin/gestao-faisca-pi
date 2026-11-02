package com.faisca.backend.tarefa.dto;

import com.faisca.backend.tarefa.entity.RegistroHoras;
import java.time.LocalDateTime;

public record RegistroHorasResponse(Integer id, Integer tarefaId, LocalDateTime inicio, LocalDateTime fim, String descricao) {
    public RegistroHorasResponse(RegistroHoras registro) {
        this(registro.getId(), registro.getTarefa().getId(), registro.getHoraInicio(), registro.getHoraFim(), registro.getDescricao());
    }
}
