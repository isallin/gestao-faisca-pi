package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.ColaboradorTarefa;
import com.faisca.backend.tarefa.entity.ColaboradorTarefaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColaboradorTarefaRepository extends JpaRepository<ColaboradorTarefa, ColaboradorTarefaId> {
    List<ColaboradorTarefa> findByIdPkTarefa(Integer tarefaId);
    List<ColaboradorTarefa> findByIdPkColaborador(Integer colaboradorId);
    boolean existsByIdPkColaborador(Integer colaboradorId);
    boolean existsByIdPkColaboradorAndIdPkTarefa(Integer colaboradorId, Integer tarefaId);
    void deleteByIdPkTarefa(Integer tarefaId);
}
