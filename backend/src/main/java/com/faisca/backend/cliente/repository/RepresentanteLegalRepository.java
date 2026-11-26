package com.faisca.backend.cliente.repository;

import com.faisca.backend.cliente.entity.RepresentanteLegal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepresentanteLegalRepository extends JpaRepository<RepresentanteLegal, Integer> {
    List<RepresentanteLegal> findByClientePjIdOrderByIdAsc(Integer clientePjId);
    Optional<RepresentanteLegal> findByIdAndClientePjId(Integer id, Integer clientePjId);
}
