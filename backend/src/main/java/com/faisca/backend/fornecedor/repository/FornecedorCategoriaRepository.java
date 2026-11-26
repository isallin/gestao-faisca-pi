package com.faisca.backend.fornecedor.repository;

import com.faisca.backend.fornecedor.entity.FornecedorCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FornecedorCategoriaRepository extends JpaRepository<FornecedorCategoria, Integer> {
    List<FornecedorCategoria> findAllByOrderByNomeAsc();
    Optional<FornecedorCategoria> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);
}
