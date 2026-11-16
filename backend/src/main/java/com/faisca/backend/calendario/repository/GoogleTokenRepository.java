package com.faisca.backend.calendario.repository;

import com.faisca.backend.calendario.entity.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Integer> {
    Optional<GoogleToken> findByUsuarioId(Integer usuarioId);
    void deleteByUsuarioId(Integer usuarioId);
}
