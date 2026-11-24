package com.faisca.backend.projeto.repository;

import com.faisca.backend.projeto.entity.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {
    List<Projeto> findAllByOrderByCodigoAsc();
    List<Projeto> findByCriadoPor(Integer criadoPor);
    Optional<Projeto> findByCodigoIgnoreCase(String codigo);
    boolean existsByFkTarefaFase(Integer fkTarefaFase);
}
