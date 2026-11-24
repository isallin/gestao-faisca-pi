package com.faisca.backend.fornecedor.repository;

import com.faisca.backend.fornecedor.entity.FornecedorEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorEmpresaRepository extends JpaRepository<FornecedorEmpresa, Integer> {
    List<FornecedorEmpresa> findAllByOrderByNomeAsc();
    boolean existsByCategoriasId(Integer categoriaId);
}
