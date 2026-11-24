package com.faisca.backend.tarefa.entity;

import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.usuario.entity.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarefa")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkProjeto", nullable = false)
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkTarefaCategoria", nullable = false)
    private TarefaCategoria categoria;

    @Column(name = "fkTarefaFase", nullable = false)
    private Integer fkTarefaFase;

    @Column(name = "nome", length = 200)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prioridade_id")
    private Prioridade prioridadeRef;

    @Column(name = "data_inicio_prev")
    private LocalDateTime dataInicioPrev;

    @Column(name = "data_fim_prev")
    private LocalDateTime dataFimPrev;

    @Column(name = "data_inicio_real")
    private LocalDateTime dataInicioReal;

    @Column(name = "data_fim_real")
    private LocalDateTime dataFimReal;

    @Column(name = "observacao", length = 300)
    private String observacao;

    /** Usuário que criou a tarefa (dono do evento no Google Calendar). */
    @Column(name = "criadoPor")
    private Integer criadoPor;

    @OneToMany(mappedBy = "tarefa", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @OrderBy("id DESC")
    @BatchSize(size = 50)
    private List<StatusTarefa> historicoStatus = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.REMOVE)
    @BatchSize(size = 50)
    private List<RegistroHoras> registrosHoras = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @BatchSize(size = 50)
    private List<ChecklistItem> checklist = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @BatchSize(size = 50)
    private List<Subtarefa> subtarefas = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<ColaboradorTarefa> atribuicoes = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<TarefaFase> tarefasFases = new ArrayList<>();

    public void adicionarStatus(StatusTarefa status) {
        status.setTarefa(this);
        historicoStatus.add(0, status);
    }

    public String getStatusAtual() {
        if (historicoStatus.isEmpty()) {
            return null;
        }
        return historicoStatus.get(0).getStatus();
    }

    /** Compatibilidade retroativa com consumidores que leem a lista de colaboradores da tarefa. */
    public List<Usuario> getColaboradores() {
        return atribuicoes.stream().map(ColaboradorTarefa::getColaborador).toList();
    }

    public String getPrioridadeNome() {
        return prioridadeRef != null ? prioridadeRef.getNome() : null;
    }

    public List<StatusTarefa> getHistoricoStatus() { return historicoStatus; }
    public List<RegistroHoras> getRegistrosHoras() { return registrosHoras; }
    public List<ChecklistItem> getChecklist() { return checklist; }
    public List<Subtarefa> getSubtarefas() { return subtarefas; }
    public List<ColaboradorTarefa> getAtribuicoes() { return atribuicoes; }
    public List<TarefaFase> getTarefasFases() { return tarefasFases; }

    public Integer getId() { return id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public TarefaCategoria getCategoria() { return categoria; }
    public void setCategoria(TarefaCategoria categoria) { this.categoria = categoria; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public void setFkTarefaFase(Integer fkTarefaFase) { this.fkTarefaFase = fkTarefaFase; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Prioridade getPrioridadeRef() { return prioridadeRef; }
    public void setPrioridadeRef(Prioridade prioridadeRef) { this.prioridadeRef = prioridadeRef; }
    public LocalDateTime getDataInicioPrev() { return dataInicioPrev; }
    public void setDataInicioPrev(LocalDateTime dataInicioPrev) { this.dataInicioPrev = dataInicioPrev; }
    public LocalDateTime getDataFimPrev() { return dataFimPrev; }
    public void setDataFimPrev(LocalDateTime dataFimPrev) { this.dataFimPrev = dataFimPrev; }
    public LocalDateTime getDataInicioReal() { return dataInicioReal; }
    public void setDataInicioReal(LocalDateTime dataInicioReal) { this.dataInicioReal = dataInicioReal; }
    public LocalDateTime getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDateTime dataFimReal) { this.dataFimReal = dataFimReal; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Integer getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Integer criadoPor) { this.criadoPor = criadoPor; }
}
