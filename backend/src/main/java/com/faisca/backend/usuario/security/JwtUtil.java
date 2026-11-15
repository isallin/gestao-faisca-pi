package com.faisca.backend.usuario.security;

import com.faisca.backend.usuario.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-minutes}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMinutos * 60 * 1000;
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();

        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("cargo", usuario.getCargo())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * State do OAuth do Google: identifica o usuário do Faísca durante o fluxo
     * (o callback chega sem o header Authorization). Vale 10 minutos e só serve
     * para o fluxo do Google.
     */
    public String gerarStateGoogle(Integer usuarioId) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claim("purpose", "google-state")
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + 10 * 60 * 1000))
                .signWith(chave)
                .compact();
    }

    public Integer validarStateGoogle(String state) {
        Claims claims = validar(state);
        if (!"google-state".equals(claims.get("purpose"))) {
            throw new JwtException("State inválido para o fluxo do Google");
        }
        return Integer.valueOf(claims.getSubject());
    }

    public long getExpiracaoSegundos() {
        return expiracaoMs / 1000;
    }
}
