package com.faisca.backend.calendario.repository;

import com.faisca.backend.calendario.entity.GoogleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoogleEventRepository extends JpaRepository<GoogleEvent, Integer> {
    List<GoogleEvent> findByTipoAndFkOrigem(String tipo, Integer fkOrigem);
    boolean existsByTipoAndFkOrigemAndFkUsuario(String tipo, Integer fkOrigem, Integer fkUsuario);
}
