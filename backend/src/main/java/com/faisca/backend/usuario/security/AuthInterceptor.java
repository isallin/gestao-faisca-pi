package com.faisca.backend.usuario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String metodo = request.getMethod();
        String caminho = request.getRequestURI();

        if (metodo.equals("OPTIONS")
                || caminho.equals("/api/auth/login")
                || (caminho.equals("/api/usuarios") && metodo.equals("POST"))) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return negar(response);
        }

        try {
            Claims claims = jwtUtil.validar(header.substring(7));
            request.setAttribute("usuarioId", Integer.valueOf(claims.getSubject()));
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return negar(response);
        }
    }

    private boolean negar(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"erro\":\"Token ausente, inválido ou expirado\"}");
        return false;
    }
}
