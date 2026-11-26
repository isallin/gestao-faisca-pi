package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.Subtarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubtarefaRepository extends JpaRepository<Subtarefa, Integer> {
    List<Subtarefa> findByTarefaIdOrderByIdAsc(Integer tarefaId);
    Optional<Subtarefa> findByIdAndTarefaId(Integer id, Integer tarefaId);
}
