package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.Prioridade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrioridadeRepository extends JpaRepository<Prioridade, Integer> {
    List<Prioridade> findAllByOrderByIdAsc();
    Optional<Prioridade> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);
}
