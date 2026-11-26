package com.faisca.backend.cliente.repository;

import com.faisca.backend.cliente.entity.EnderecoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoClienteRepository extends JpaRepository<EnderecoCliente, Integer> {
    List<EnderecoCliente> findByClienteIdOrderByIdAsc(Integer clienteId);
    Optional<EnderecoCliente> findByIdAndClienteId(Integer id, Integer clienteId);
}
