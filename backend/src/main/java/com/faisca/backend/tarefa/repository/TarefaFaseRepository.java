package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.TarefaFase;
import com.faisca.backend.tarefa.entity.TarefaFaseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaFaseRepository extends JpaRepository<TarefaFase, TarefaFaseId> {
    List<TarefaFase> findByIdIdProjeto(Integer projetoId);
    List<TarefaFase> findByIdIdFase(Integer faseId);
    List<TarefaFase> findByIdPkTarefa(Integer tarefaId);
    boolean existsByIdIdFase(Integer faseId);
    void deleteByIdPkTarefa(Integer tarefaId);
}
