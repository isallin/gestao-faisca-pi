package com.faisca.backend.cliente.repository;

import com.faisca.backend.cliente.entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByPessoaFisicaCpfAndIdNot(String cpf, Integer id);
    boolean existsByPessoaJuridicaCnpjAndIdNot(String cnpj, Integer id);
    boolean existsByPessoaFisicaCpf(String cpf);

    boolean existsByPessoaJuridicaCnpj(String cnpj);
}
