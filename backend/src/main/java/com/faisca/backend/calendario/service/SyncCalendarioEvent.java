package com.faisca.backend.calendario.service;

/**
 * Publicado pelos services quando uma entidade é criada/atualizada/excluída.
 * O listener {@link CalendarSyncService} roda depois do commit e conversa com o Google,
 * para que uma falha do Google nunca derrube a operação do Faísca.
 */
public record SyncCalendarioEvent(TipoEntidade tipo, Integer origemId,
                                  Integer usuarioId, Acao acao, EventoCalendario dados) {

    public enum TipoEntidade { TAREFA, PROJETO, REGISTRO_HORAS }

    public enum Acao { CRIAR, ATUALIZAR, EXCLUIR }
}
