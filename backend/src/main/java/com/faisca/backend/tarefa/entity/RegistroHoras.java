package com.faisca.backend.tarefa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_horas")
public class RegistroHoras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkTarefa", nullable = false)
    private Tarefa tarefa;

    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fim")
    private LocalDateTime horaFim;

    public LocalDateTime getHoraInicio() { return horaInicio; }
    public LocalDateTime getHoraFim() { return horaFim; }
}
