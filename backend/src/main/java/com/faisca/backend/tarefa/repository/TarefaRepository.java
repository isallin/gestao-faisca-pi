package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
    boolean existsByProjetoId(Integer projetoId);
    List<Tarefa> findByProjetoCodigo(String codigo);
}
