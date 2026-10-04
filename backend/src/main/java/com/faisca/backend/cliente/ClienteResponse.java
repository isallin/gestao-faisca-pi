package com.faisca.backend.cliente;


public class ClienteResponse {

    private Integer id;
    private String name;
    private String type;
    private String projects;
    private String address;
    private String phone;
    private String email;
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
