package com.faisca.backend.cliente.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;


public class ClienteResponse {

    @Schema(description = "ID do cliente", example = "1") private Integer id;
    @Schema(description = "Nome ou nome fantasia", example = "João Silva") private String name;
    @Schema(description = "Tipo apresentado", allowableValues = {"Pessoa Física", "Pessoa Jurídica"}, example = "Pessoa Física") private String type;
    @Schema(description = "Códigos dos projetos separados por vírgula", example = "PRJ-001, PRJ-002") private String projects;
    @Schema(description = "Endereço formatado", example = "Avenida Paulista, 100 — Bela Vista, São Paulo-SP, 01310-100") private String address;
    @Schema(description = "Telefone formatado", example = "(11) 98765-4321") private String phone;
    @Schema(description = "E-mail", example = "joao@email.com") private String email;
    @Schema(description = "Observações", example = "Contato pela manhã") private String note;
    @Schema(description = "Detalhes variáveis conforme PF/PJ: phone, email, note, zip, street, number, complement, neighborhood, city, state, country; PF: name, last, cpf, rg, birth, marital, job; PJ: company, cnpj, repName, repCpf, repRole, repEmail, repPhone") private Map<String, Object> details;

    public ClienteResponse(Integer id, String name, String type, String projects, String address, String phone, String email, String note) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.projects = projects;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getProjects() {
        return projects;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getNote() {
        return note;
    }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
