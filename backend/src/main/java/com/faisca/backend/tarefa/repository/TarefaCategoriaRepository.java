package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.TarefaCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaCategoriaRepository extends JpaRepository<TarefaCategoria, Integer> {
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);
}
