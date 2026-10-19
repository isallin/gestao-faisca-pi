package com.faisca.backend.login;


public class UsuarioResponse {

    private Integer id;
    private String nome;
    private String sobrenome;
    private String email;
    private String cargo;

    public UsuarioResponse(Integer id, String nome, String sobrenome, String email, String cargo) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.cargo = cargo;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }
}
