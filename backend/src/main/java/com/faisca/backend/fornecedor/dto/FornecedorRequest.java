package com.faisca.backend.fornecedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class FornecedorRequest {

    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    @Schema(description = "Nome do fornecedor (compatibilidade legada)", example = "Materiais Central")
    private String name;

    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    @Schema(description = "Nome da empresa fornecedora (V8)", example = "Materiais Central")
    private String nome;

    @Size(max = 80, message = "Categoria deve ter no máximo 80 caracteres")
    @Schema(description = "Categoria principal (compatibilidade legada)", example = "Materiais")
    private String category;

    @Size(max = 80, message = "Categoria deve ter no máximo 80 caracteres")
    @Schema(description = "Categoria principal (alias PT-BR)", example = "Materiais")
    private String categoria;

    @Size(max = 20, message = "Telefone 1 deve ter no máximo 20 caracteres")
    @Schema(description = "Telefone principal (obrigatório quando contatos não é informado)", example = "(11) 98765-4321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Pattern(regexp = "^$|^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone 2 inválido")
    @Size(max = 20, message = "Telefone 2 deve ter no máximo 20 caracteres")
    @Schema(description = "Telefone alternativo opcional", example = "(11) 3456-7890")
    private String phone2;

    @Email(message = "E-mail 1 inválido")
    @Size(max = 120, message = "E-mail 1 deve ter no máximo 120 caracteres")
    @Schema(description = "E-mail principal (obrigatório quando contatos não é informado)", example = "contato@materiais.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Email(message = "E-mail 2 inválido")
    @Size(max = 120, message = "E-mail 2 deve ter no máximo 120 caracteres")
    @Schema(description = "E-mail alternativo opcional", example = "vendas@materiais.com")
    private String email2;

    @Size(max = 80, message = "Cidade deve ter no máximo 80 caracteres")
    @Schema(description = "Cidade (obrigatória caso 'cidade' não seja informada)", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @Size(max = 80, message = "Cidade deve ter no máximo 80 caracteres")
    @Schema(description = "Cidade (V8; obrigatória caso 'city' não seja informada)", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @Size(max = 80)
    @Schema(description = "Origem/indicação do fornecedor (V8)", example = "Indicação de cliente")
    private String origem;

    @Pattern(regexp = "^$|^[A-Za-z]{2}$", message = "Use a sigla da UF (ex.: SP)")
    @Schema(description = "UF do fornecedor (V8)", example = "SP")
    private String estado;

    @Schema(description = "Indica se já trabalhou com o fornecedor (V8)", example = "true")
    private Boolean jaTrabalhei;

    @Size(max = 255)
    @Schema(description = "Códigos de projetos separados por vírgula ou ponto e vírgula (compatibilidade legada)", example = "RES-024")
    private String projects;

    @Size(max = 300)
    @Schema(description = "Observação (compatibilidade legada)", example = "Entregas em dias úteis")
    private String note;

    @Size(max = 300)
    @Schema(description = "Observação (V8)", example = "Entregas em dias úteis")
    private String observacao;

    @Schema(description = "IDs das categorias associadas (V8 N:N)")
    private List<Integer> categoriasIds;

    @Schema(description = "Nomes das categorias associadas (V8 N:N)")
    private List<String> categorias;

    @Schema(description = "Lista de contatos do fornecedor (V8 1:N)")
    private List<@Valid FornecedorContatoRequest> contatos;

    @Schema(description = "Lista de projetos vinculados ao fornecedor (V8 N:N)")
    private List<@Valid FornecedorProjetoRequest> projetosVinculados;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
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
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Boolean getJaTrabalhei() { return jaTrabalhei; }
    public void setJaTrabalhei(Boolean jaTrabalhei) { this.jaTrabalhei = jaTrabalhei; }
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<Integer> getCategoriasIds() { return categoriasIds; }
    public void setCategoriasIds(List<Integer> categoriasIds) { this.categoriasIds = categoriasIds; }
    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }
    public List<FornecedorContatoRequest> getContatos() { return contatos; }
    public void setContatos(List<FornecedorContatoRequest> contatos) { this.contatos = contatos; }
    public List<FornecedorProjetoRequest> getProjetosVinculados() { return projetosVinculados; }
    public void setProjetosVinculados(List<FornecedorProjetoRequest> projetosVinculados) { this.projetosVinculados = projetosVinculados; }
}
