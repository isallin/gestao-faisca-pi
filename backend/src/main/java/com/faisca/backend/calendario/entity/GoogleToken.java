package com.faisca.backend.calendario.entity;

import jakarta.persistence.*;

/**
 * Token OAuth do Google Calendar de um usuário do sistema.
 * As credenciais ficam no banco (a POC guardava em memória).
 */
@Entity
@Table(name = "google_token")
public class GoogleToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "fkUsuario", nullable = false, unique = true)
    private Integer usuarioId;

    @Column(name = "access_token", length = 2048)
    private String accessToken;

    @Column(name = "refresh_token", length = 2048)
    private String refreshToken;

    /** Epoch millis em que o access token expira. */
    @Column(name = "expira_em")
    private Long expiraEm;

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Long getExpiraEm() { return expiraEm; }
    public void setExpiraEm(Long expiraEm) { this.expiraEm = expiraEm; }
}
