package com.faisca.backend.calendario.service;

import com.faisca.backend.calendario.entity.GoogleToken;
import com.faisca.backend.calendario.repository.GoogleTokenRepository;
import com.faisca.backend.usuario.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Cuida do OAuth do Google por usuário: monta a URL de autorização,
 * troca o código por tokens, guarda no banco e renova o access token.
 */
@Service
public class GoogleAuthService {

    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/calendar");
    private static final String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final GoogleTokenRepository tokens;
    private final JwtUtil jwtUtil;

    public GoogleAuthService(GoogleTokenRepository tokens, JwtUtil jwtUtil) {
        this.tokens = tokens;
        this.jwtUtil = jwtUtil;
    }

    /** URL do Google para o usuário logar e permitir o acesso. O state identifica o usuário do Faísca. */
    public String montarUrlAutorizacao(Integer usuarioId) throws IOException, GeneralSecurityException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                clientId, clientSecret, SCOPES)
                .setAccessType("offline")
                .build();

        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(jwtUtil.gerarStateGoogle(usuarioId))
                .build();
    }

    /** Troca o código devolvido pelo Google por access/refresh token e salva no banco. */
    public void conectar(Integer usuarioId, String code) throws IOException, GeneralSecurityException {
        GoogleTokenResponse resposta = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                TOKEN_SERVER_URL, clientId, clientSecret, code, redirectUri)
                .execute();

        GoogleToken token = tokens.findByUsuarioId(usuarioId).orElseGet(() -> {
            GoogleToken novo = new GoogleToken();
            novo.setUsuarioId(usuarioId);
            return novo;
        });
        token.setAccessToken(resposta.getAccessToken());
        // O Google só devolve refresh_token na primeira autorização; manter o antigo se vier nulo.
        if (resposta.getRefreshToken() != null) token.setRefreshToken(resposta.getRefreshToken());
        long validadeSegundos = resposta.getExpiresInSeconds() != null ? resposta.getExpiresInSeconds() : 3600L;
        token.setExpiraEm(System.currentTimeMillis() + validadeSegundos * 1000);
        tokens.save(token);
    }

    public boolean isConectado(Integer usuarioId) {
        return usuarioId != null && tokens.findByUsuarioId(usuarioId).isPresent();
    }

    public void desconectar(Integer usuarioId) {
        tokens.deleteByUsuarioId(usuarioId);
    }

    /** Cliente da Calendar API autenticado como o usuário (renova o token se estiver perto de expirar). */
    public Calendar obterCalendar(Integer usuarioId) throws IOException {
        String accessToken = obterAccessToken(usuarioId);
        HttpRequestInitializer initializer =
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken);
        return new Calendar.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), initializer)
                .setApplicationName("Faisca Arquitetura")
                .build();
    }

    private String obterAccessToken(Integer usuarioId) throws IOException {
        GoogleToken token = tokens.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Google Calendar não conectado"));

        boolean valido = token.getExpiraEm() != null
                && token.getExpiraEm() > System.currentTimeMillis() + 60_000;
        if (valido) return token.getAccessToken();

        if (token.getRefreshToken() == null) {
            throw new IllegalStateException("Sessão do Google expirada; reconecte o Google Calendar");
        }

        GoogleTokenResponse resposta = new GoogleRefreshTokenRequest(
                new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                clientId, clientSecret, token.getRefreshToken())
                .execute();
        token.setAccessToken(resposta.getAccessToken());
        long validadeSegundos = resposta.getExpiresInSeconds() != null ? resposta.getExpiresInSeconds() : 3600L;
        token.setExpiraEm(System.currentTimeMillis() + validadeSegundos * 1000);
        tokens.save(token);
        return token.getAccessToken();
    }
}
