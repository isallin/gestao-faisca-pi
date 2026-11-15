package com.faisca.backend.tarefa.repository;

import com.faisca.backend.tarefa.entity.RegistroHoras;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroHorasRepository extends JpaRepository<RegistroHoras, Integer> {
    java.util.List<RegistroHoras> findByCriadoPor(Integer criadoPor);
}
