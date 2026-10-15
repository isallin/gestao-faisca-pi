package com.faisca.backend.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByPessoaFisicaCpf(String cpf);

    boolean existsByPessoaJuridicaCnpj(String cnpj);
}
