package com.faisca.backend.usuario.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtStateGoogleTests {

    private static final String SEGUNDO = "chave-de-teste-super-secreta-1234567890";

    private final JwtUtil jwt = new JwtUtil(SEGUNDO, 60);

    @Test
    void stateValidoIdentificaUsuario() {
        String state = jwt.gerarStateGoogle(42);
        assertEquals(42, jwt.validarStateGoogle(state));
    }

    @Test
    void stateForjadoEhRejeitado() {
        JwtUtil outraChave = new JwtUtil("outra-chave-completamente-diferente-123456", 60);
        String state = outraChave.gerarStateGoogle(1);

        assertThrows(JwtException.class, () -> jwt.validarStateGoogle(state));
    }

    @Test
    void tokenSemPurposeGoogleEhRejeitado() {
        String tokenDeLogin = Jwts.builder()
                .subject("1")
                .signWith(Keys.hmacShaKeyFor(SEGUNDO.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(JwtException.class, () -> jwt.validarStateGoogle(tokenDeLogin));
    }
}
