package com.faisca.backend.projeto.repository;

import com.faisca.backend.projeto.entity.Projeto;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {
    List<Projeto> findAllByOrderByCodigoAsc();
}
