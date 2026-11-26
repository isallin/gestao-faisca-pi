package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.RegistroHoras;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroHorasRepository extends JpaRepository<RegistroHoras, Integer> {
    List<RegistroHoras> findByCriadoPor(Integer criadoPor);
    List<RegistroHoras> findByTarefaId(Integer tarefaId);
    List<RegistroHoras> findByColaboradorId(Integer colaboradorId);
    List<RegistroHoras> findByTarefaIdAndColaboradorId(Integer tarefaId, Integer colaboradorId);
    boolean existsByColaboradorId(Integer colaboradorId);
    boolean existsByColaboradorIdAndTarefaId(Integer colaboradorId, Integer tarefaId);
}
