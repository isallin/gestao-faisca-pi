package com.faisca.backend.projeto;

import com.faisca.backend.cliente.Cliente;
import org.hibernate.annotations.Immutable;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "projeto")
public class Projeto {

    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fkCliente")
    private Cliente cliente;

    @Column(name = "codigo", length = 45)
    private String codigo;

    public Integer getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public String getCodigo() { return codigo; }
}
