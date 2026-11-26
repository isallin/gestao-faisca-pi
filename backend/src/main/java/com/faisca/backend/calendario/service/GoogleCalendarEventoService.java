package com.faisca.backend.calendario.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Operações de baixo nível no Google Calendar (criar/atualizar/remover eventos).
 * Reaproveita o que funcionou na POC: evento de dia inteiro com fim exclusivo
 * (start + 1 dia) e validação de data de fim anterior ao início.
 */
@Service
public class GoogleCalendarEventoService {

    private static final String CALENDAR_ID = "primary";

    private final GoogleAuthService auth;

    public GoogleCalendarEventoService(GoogleAuthService auth) {
        this.auth = auth;
    }

    public String criar(Integer usuarioId, EventoCalendario dados)
            throws IOException, GeneralSecurityException {
        Event criado = auth.obterCalendar(usuarioId)
                .events().insert(CALENDAR_ID, montarEvento(dados)).execute();
        return criado.getId();
    }

    /**
     * Atualiza o evento; se o usuário o tiver apagado direto no Google (404),
     * recria e devolve o novo id.
     */
    public String atualizar(Integer usuarioId, String googleEventId, EventoCalendario dados)
            throws IOException, GeneralSecurityException {
        Calendar calendar = auth.obterCalendar(usuarioId);
        try {
            Event atualizado = montarEvento(dados);
            atualizado.setId(googleEventId);
            calendar.events().update(CALENDAR_ID, googleEventId, atualizado).execute();
            return googleEventId;
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) return criar(usuarioId, dados);
            throw e;
        }
    }

    /** Remove o evento; se já não existir (404), não faz nada. */
    public void remover(Integer usuarioId, String googleEventId)
            throws IOException, GeneralSecurityException {
        try {
            auth.obterCalendar(usuarioId).events().delete(CALENDAR_ID, googleEventId).execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() != 404) throw e;
        }
    }

    /** Monta o evento do Google; visível para testes. */
    Event montarEvento(EventoCalendario dados) {
        Event event = new Event()
                .setSummary(dados.summary())
                .setDescription(dados.descricao() == null ? "" : dados.descricao());

        if (dados.horariado()) {
            event.setStart(new EventDateTime().setDateTime(paraDateTime(dados.inicio())));
            event.setEnd(new EventDateTime().setDateTime(paraDateTime(dados.fim())));
            return event;
        }

        LocalDate inicio = dados.inicio().toLocalDate();
        LocalDate fim = dados.fim().toLocalDate();
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data de fim deve ser igual ou posterior à data de início");
        }
        // O Google trata o fim de evento de dia inteiro como EXCLUSIVO: o último dia
        // do período (inclusive no Faísca) precisa ser enviado como dia + 1, senão o
        // evento aparece na agenda com um dia a menos.
        LocalDate fimExclusivo = fim.plusDays(1);
        event.setStart(new EventDateTime().setDate(new DateTime(inicio.toString())));
        event.setEnd(new EventDateTime().setDate(new DateTime(fimExclusivo.toString())));
        return event;
    }

    private DateTime paraDateTime(java.time.LocalDateTime local) {
        Date data = Date.from(local.atZone(ZoneId.systemDefault()).toInstant());
        return new DateTime(data);
    }
}
