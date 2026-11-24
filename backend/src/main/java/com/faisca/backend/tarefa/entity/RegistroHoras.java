package com.faisca.backend.tarefa.entity;

import com.faisca.backend.usuario.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_horas")
public class RegistroHoras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkColaborador")
    private Usuario colaborador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkTarefa", nullable = false)
    private Tarefa tarefa;

    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fim")
    private LocalDateTime horaFim;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    @Column(name = "descricao", length = 300)
    private String descricao;

    /** Usuário que criou o registro (dono do evento no Google Calendar). */
    @Column(name = "criadoPor")
    private Integer criadoPor;

    @PrePersist
    @PreUpdate
    void atualizarTimestamp() {
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public Usuario getColaborador() { return colaborador; }
    public void setColaborador(Usuario colaborador) { this.colaborador = colaborador; }
    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalDateTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalDateTime horaFim) { this.horaFim = horaFim; }
    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Integer criadoPor) { this.criadoPor = criadoPor; }
}
