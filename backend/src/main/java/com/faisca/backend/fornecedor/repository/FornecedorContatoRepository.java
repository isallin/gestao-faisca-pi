package com.faisca.backend.fornecedor.repository;

import com.faisca.backend.fornecedor.entity.FornecedorContato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FornecedorContatoRepository extends JpaRepository<FornecedorContato, Integer> {
    List<FornecedorContato> findByFornecedorIdOrderByIdAsc(Integer fornecedorId);
    Optional<FornecedorContato> findByIdAndFornecedorId(Integer id, Integer fornecedorId);
}
