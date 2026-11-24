package com.faisca.backend.usuario.entity;

import com.faisca.backend.tarefa.entity.ColaboradorTarefa;
import com.faisca.backend.tarefa.entity.RegistroHoras;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 70)
    private String nome;

    @Column(name = "sobrenome", nullable = false, length = 45)
    private String sobrenome;

    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Column(name = "cargo", length = 45)
    private String cargo;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @OneToMany(mappedBy = "colaborador")
    @BatchSize(size = 50)
    private List<ColaboradorTarefa> tarefasAtribuidas = new ArrayList<>();

    @OneToMany(mappedBy = "colaborador")
    @BatchSize(size = 50)
    private List<RegistroHoras> registrosHoras = new ArrayList<>();

    public Usuario() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public List<ColaboradorTarefa> getTarefasAtribuidas() { return tarefasAtribuidas; }
    public List<RegistroHoras> getRegistrosHoras() { return registrosHoras; }
}
