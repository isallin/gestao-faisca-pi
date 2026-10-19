package com.faisca.backend.login;


public class LoginResponse {

    private String token;
    private String tipo;
    private Long expiraEmSegundos;
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
