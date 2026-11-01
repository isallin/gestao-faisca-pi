package com.faisca.backend.cliente;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Dados de um cliente retornados pela API")
public class ClienteResponse {

    @Schema(description = "Identificador do cliente", example = "1")
    private Integer id;
    @Schema(description = "Nome do cliente ou nome fantasia da empresa", example = "João Silva")
    private String name;
    @Schema(description = "Tipo do cliente", example = "Pessoa Física")
    private String type;
    @Schema(description = "Códigos dos projetos vinculados ao cliente", example = "PROJ001, PROJ002")
    private String projects;
    @Schema(description = "Endereço do cliente", example = "Rua das Flores, 100 - Centro, São Paulo-SP")
    private String address;
    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String phone;
    @Schema(description = "E-mail do cliente", example = "joao@email.com")
    private String email;
    @Schema(description = "Observações sobre o cliente", example = "Cliente interessado em reforma residencial")
    private String note;

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
}
