package com.faisca.backend.projeto;

import com.faisca.backend.cliente.Cliente;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projeto")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fkCliente", nullable = false)
    private Cliente cliente;

    @Column(name = "codigo", nullable = false, length = 45)
    private String codigo;

    @Column(name = "relacao_proprietario", length = 80)
    private String relacaoProprietario;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "tipo_imovel", length = 45)
    private String tipoImovel;

    @Column(name = "area_m2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Column(name = "unidade_numero", length = 20)
    private String unidadeNumero;

    @Column(name = "condominio", length = 100)
    private String condominio;

    @Column(name = "matricula", length = 50)
    private String matricula;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "observacao", length = 300)
    private String observacao;

    // ID opcional: o relacionamento com fases/tarefas ainda não foi definido.
    @Column(name = "fkTarefaFase")
    private Integer fkTarefaFase;

    public Integer getId() { return id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getRelacaoProprietario() { return relacaoProprietario; }
    public void setRelacaoProprietario(String relacaoProprietario) { this.relacaoProprietario = relacaoProprietario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoImovel() { return tipoImovel; }
    public void setTipoImovel(String tipoImovel) { this.tipoImovel = tipoImovel; }

    public BigDecimal getAreaM2() { return areaM2; }
    public void setAreaM2(BigDecimal areaM2) { this.areaM2 = areaM2; }

    public String getUnidadeNumero() { return unidadeNumero; }
    public void setUnidadeNumero(String unidadeNumero) { this.unidadeNumero = unidadeNumero; }

    public String getCondominio() { return condominio; }
    public void setCondominio(String condominio) { this.condominio = condominio; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public void setFkTarefaFase(Integer fkTarefaFase) { this.fkTarefaFase = fkTarefaFase; }

}
