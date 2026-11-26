package com.faisca.backend.projeto.repository;

import com.faisca.backend.projeto.entity.EnderecoProjeto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoProjetoRepository extends JpaRepository<EnderecoProjeto, Integer> {
    Optional<EnderecoProjeto> findByProjetoId(Integer projetoId);
}
