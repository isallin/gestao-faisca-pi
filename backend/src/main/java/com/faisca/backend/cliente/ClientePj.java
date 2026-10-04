package com.faisca.backend.cliente;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente_pj")
public class ClientePj {

    @Id
    @Column(name = "cliente_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "cnpj", nullable = false, length = 14)
    private String cnpj;

    @Column(name = "nome_fantasia", nullable = false, length = 140)
    private String nomeFantasia;

    @OneToMany(mappedBy = "clientePj", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepresentanteLegal> representantes = new ArrayList<>();

    public void addRepresentante(RepresentanteLegal representante) {
        representante.setClientePj(this);
        this.representantes.add(representante);
    }

    public Integer getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public List<RepresentanteLegal> getRepresentantes() { return representantes; }
}
