package com.faisca.backend.calendario.entity;

import jakarta.persistence.*;

/**
 * Vincula uma entidade do Faísca (tarefa, projeto ou registro de horas)
 * ao id do evento criado no Google Calendar do usuário criador.
 */
@Entity
@Table(name = "google_event",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tipo", "fkOrigem", "fkUsuario"}))
public class GoogleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** TAREFA, PROJETO ou REGISTRO_HORAS. */
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "fkOrigem", nullable = false)
    private Integer fkOrigem;

    @Column(name = "fkUsuario", nullable = false)
    private Integer fkUsuario;

    @Column(name = "google_event_id", nullable = false, length = 255)
    private String googleEventId;

    public GoogleEvent() { }

    public GoogleEvent(String tipo, Integer fkOrigem, Integer fkUsuario, String googleEventId) {
        this.tipo = tipo;
        this.fkOrigem = fkOrigem;
        this.fkUsuario = fkUsuario;
        this.googleEventId = googleEventId;
    }

    public Integer getId() { return id; }
    public String getTipo() { return tipo; }
    public Integer getFkOrigem() { return fkOrigem; }
    public Integer getFkUsuario() { return fkUsuario; }
    public String getGoogleEventId() { return googleEventId; }
    public void setGoogleEventId(String googleEventId) { this.googleEventId = googleEventId; }
}
