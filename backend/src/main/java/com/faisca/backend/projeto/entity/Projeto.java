package com.faisca.backend.projeto.entity;

import com.faisca.backend.cliente.entity.Cliente;
import com.faisca.backend.fornecedor.entity.FornecedorProjeto;
import com.faisca.backend.tarefa.entity.TarefaFase;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "cep", length = 8) private String cep;
    @Column(name = "logradouro", length = 200) private String logradouro;
    @Column(name = "numero", length = 10) private String numero;
    @Column(name = "complemento", length = 60) private String complemento;
    @Column(name = "bairro", length = 80) private String bairro;
    @Column(name = "cidade", length = 80) private String cidade;
    @Column(name = "estado", length = 2) private String estado;
    @Column(name = "pais", length = 80) private String pais;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "observacao", length = 300)
    private String observacao;

    /** Usuário que criou o projeto (dono do evento no Google Calendar). */
    @Column(name = "criadoPor")
    private Integer criadoPor;

    @Column(name = "fkTarefaFase")
    private Integer fkTarefaFase;

    @OneToOne(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnderecoProjeto enderecoProjeto;

    @OneToMany(mappedBy = "projeto")
    @BatchSize(size = 50)
    private List<FornecedorProjeto> fornecedores = new ArrayList<>();

    @OneToMany(mappedBy = "projeto")
    @BatchSize(size = 50)
    private List<TarefaFase> tarefasFases = new ArrayList<>();

    public void setEnderecoProjeto(EnderecoProjeto enderecoProjeto) {
        this.enderecoProjeto = enderecoProjeto;
        if (enderecoProjeto != null) {
            enderecoProjeto.setProjeto(this);
        }
    }

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
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Integer getFkTarefaFase() { return fkTarefaFase; }
    public void setFkTarefaFase(Integer fkTarefaFase) { this.fkTarefaFase = fkTarefaFase; }

    public Integer getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Integer criadoPor) { this.criadoPor = criadoPor; }

    public EnderecoProjeto getEnderecoProjeto() { return enderecoProjeto; }
    public List<FornecedorProjeto> getFornecedores() { return fornecedores; }
    public List<TarefaFase> getTarefasFases() { return tarefasFases; }
}
