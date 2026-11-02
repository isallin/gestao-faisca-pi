package com.faisca.backend.fornecedor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank @Size(max = 150) @Column(name = "nome")
    private String name;
    @NotBlank @Size(max = 80) @Column(name = "categoria")
    private String category;
    @Size(max = 20) @Column(name = "telefone")
    private String phone;
    @Size(max = 20) @Column(name = "telefone2")
    private String phone2;
    @Email @Size(max = 120) @Column(name = "email")
    private String email;
    @Email @Size(max = 120) @Column(name = "email2")
    private String email2;
    @Size(max = 80) @Column(name = "cidade")
    private String city;
    @Size(max = 255) @Column(name = "projetos")
    private String projects;
    @Size(max = 300) @Column(name = "observacao")
    private String note;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone2() { return phone2; }
    public void setPhone2(String phone2) { this.phone2 = phone2; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail2() { return email2; }
    public void setEmail2(String email2) { this.email2 = email2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
