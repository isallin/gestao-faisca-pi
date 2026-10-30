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

    @Column(name = "prioridade", length = 45)
    private String prioridade;

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

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.PERSIST)
    @OrderBy("id DESC")
    @BatchSize(size = 50)
    private List<StatusTarefa> historicoStatus = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa")
    @BatchSize(size = 50)
    private List<RegistroHoras> registrosHoras = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "colaborador_tarefa",
            joinColumns = @JoinColumn(name = "pkTarefa"),
            inverseJoinColumns = @JoinColumn(name = "pkColaborador"))
    @BatchSize(size = 50)
    private List<Usuario> colaboradores = new ArrayList<>();

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

    public List<StatusTarefa> getHistoricoStatus() { return historicoStatus; }
    public List<RegistroHoras> getRegistrosHoras() { return registrosHoras; }
    public List<Usuario> getColaboradores() { return colaboradores; }

    public Integer getId() { return id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public TarefaCategoria getCategoria() { return categoria; }
    public void setCategoria(TarefaCategoria categoria) { this.categoria = categoria; }
    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public void setFkTarefaFase(Integer fkTarefaFase) { this.fkTarefaFase = fkTarefaFase; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
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
}
