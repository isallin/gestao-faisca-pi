package com.faisca.backend.fornecedor.repository;

import com.faisca.backend.fornecedor.entity.FornecedorProjeto;
import com.faisca.backend.fornecedor.entity.FornecedorProjetoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorProjetoRepository extends JpaRepository<FornecedorProjeto, FornecedorProjetoId> {
    List<FornecedorProjeto> findByFornecedorId(Integer fornecedorId);
    List<FornecedorProjeto> findByProjetoId(Integer projetoId);
    boolean existsByProjetoId(Integer projetoId);
}
