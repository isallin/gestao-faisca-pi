package com.faisca.backend.fornecedor.entity;

import com.faisca.backend.projeto.entity.Projeto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fornecedor_projeto")
public class FornecedorProjeto {

    @EmbeddedId
    private FornecedorProjetoId id = new FornecedorProjetoId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("fornecedorId")
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private FornecedorEmpresa fornecedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projetoId")
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @Column(name = "servico", length = 200)
    private String servico;

    @Column(name = "valor", precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    public FornecedorProjetoId getId() { return id; }
    public void setId(FornecedorProjetoId id) { this.id = id; }
    public FornecedorEmpresa getFornecedor() { return fornecedor; }
    public void setFornecedor(FornecedorEmpresa fornecedor) {
        this.fornecedor = fornecedor;
        if (fornecedor != null && fornecedor.getId() != null) {
            this.id.setFornecedorId(fornecedor.getId());
        }
    }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        if (projeto != null && projeto.getId() != null) {
            this.id.setProjetoId(projeto.getId());
        }
    }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
}
