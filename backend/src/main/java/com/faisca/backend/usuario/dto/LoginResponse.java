package com.faisca.backend.usuario.dto;
import io.swagger.v3.oas.annotations.media.Schema;


public class LoginResponse {

    @Schema(description = "Token JWT emitido; use no botão Authorize", accessMode = Schema.AccessMode.READ_ONLY)
    private String token;
    @Schema(description = "Tipo do token", example = "Bearer")
    private String tipo;
    @Schema(description = "Validade do token em segundos", example = "3600")
    private Long expiraEmSegundos;
    @Schema(description = "Dados públicos do usuário autenticado")
    private UsuarioResponse usuario;

    public LoginResponse(String token, String tipo, Long expiraEmSegundos, UsuarioResponse usuario) {
        this.token = token;
        this.tipo = tipo;
        this.expiraEmSegundos = expiraEmSegundos;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public Long getExpiraEmSegundos() {
        return expiraEmSegundos;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }
}
