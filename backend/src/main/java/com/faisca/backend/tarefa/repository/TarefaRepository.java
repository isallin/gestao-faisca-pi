package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
    boolean existsByProjetoId(Integer projetoId);
    boolean existsByCategoriaId(Integer categoriaId);
    boolean existsByFkTarefaFase(Integer fkTarefaFase);
    boolean existsByPrioridadeRefId(Integer prioridadeId);
    List<Tarefa> findByProjetoCodigo(String codigo);
    List<Tarefa> findByCriadoPor(Integer criadoPor);
}
