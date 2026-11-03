package com.faisca.backend.fornecedor;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID gerado pelo servidor", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;
    @NotBlank @Size(max = 150) @Column(name = "nome")
    @Schema(description = "Nome do fornecedor", example = "Materiais Central", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @NotBlank @Size(max = 80) @Column(name = "categoria")
    @Schema(description = "Categoria", example = "Materiais", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;
    @Size(max = 20) @Column(name = "telefone")
    @Schema(description = "Telefone principal", example = "11987654321")
    private String phone;
    @Size(max = 20) @Column(name = "telefone2")
    @Schema(description = "Telefone alternativo", example = "1134567890")
    private String phone2;
    @Email @Size(max = 120) @Column(name = "email")
    @Schema(description = "E-mail principal", example = "contato@materiais.com")
    private String email;
    @Email @Size(max = 120) @Column(name = "email2")
    @Schema(description = "E-mail alternativo", example = "vendas@materiais.com")
    private String email2;
    @Size(max = 80) @Column(name = "cidade")
    @Schema(description = "Cidade", example = "São Paulo")
    private String city;
    @Size(max = 255) @Column(name = "projetos")
    @Schema(description = "Referência textual de projetos", example = "Reforma Central")
    private String projects;
    @Size(max = 300) @Column(name = "observacao")
    @Schema(description = "Observação", example = "Entregas em dias úteis")
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
