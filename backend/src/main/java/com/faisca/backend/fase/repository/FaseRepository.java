package com.faisca.backend.fase.repository;

import com.faisca.backend.fase.entity.Fase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaseRepository extends JpaRepository<Fase, Integer> {
    List<Fase> findAllByOrderByOrdemAsc();
}
