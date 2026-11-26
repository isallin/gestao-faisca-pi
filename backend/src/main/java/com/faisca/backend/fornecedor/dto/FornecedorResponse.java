package com.faisca.backend.fornecedor.dto;

import com.faisca.backend.fornecedor.entity.FornecedorContato;
import com.faisca.backend.fornecedor.entity.FornecedorEmpresa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

public class FornecedorResponse {

    @Schema(description = "ID do fornecedor", example = "1") private final Integer id;
    @Schema(description = "Nome do fornecedor (compatibilidade)", example = "Materiais Central") private final String name;
    @Schema(description = "Nome da empresa fornecedora (V8)", example = "Materiais Central") private final String nome;
    @Schema(description = "Categoria principal (compatibilidade)", example = "Materiais") private final String category;
    @Schema(description = "Telefone principal (compatibilidade)", example = "(11) 98765-4321") private final String phone;
    @Schema(description = "Telefone alternativo (compatibilidade)", example = "(11) 3456-7890") private final String phone2;
    @Schema(description = "E-mail principal (compatibilidade)", example = "contato@materiais.com") private final String email;
    @Schema(description = "E-mail alternativo (compatibilidade)", example = "vendas@materiais.com") private final String email2;
    @Schema(description = "Cidade (compatibilidade)", example = "São Paulo") private final String city;
    @Schema(description = "Cidade (V8)", example = "São Paulo") private final String cidade;
    @Schema(description = "Origem/indicação do fornecedor (V8)", example = "Indicação") private final String origem;
    @Schema(description = "UF do fornecedor (V8)", example = "SP") private final String estado;
    @Schema(description = "Se já trabalhou com o fornecedor (V8)", example = "true") private final Boolean jaTrabalhei;
    @Schema(description = "Códigos dos projetos vinculados separados por vírgula", example = "RES-024, INT-032") private final String projects;
    @Schema(description = "Observação (compatibilidade)", example = "Entregas em dias úteis") private final String note;
    @Schema(description = "Observação (V8)", example = "Entregas em dias úteis") private final String observacao;
    @Schema(description = "Categorias associadas (V8 N:N)") private final List<FornecedorCategoriaResponse> categorias;
    @Schema(description = "Contatos do fornecedor (V8 1:N)") private final List<FornecedorContatoResponse> contatos;
    @Schema(description = "Projetos vinculados ao fornecedor (V8 N:N)") private final List<FornecedorProjetoResponse> projetosVinculados;

    public FornecedorResponse(FornecedorEmpresa empresa) {
        this.id = empresa.getId();
        this.name = empresa.getNome();
        this.nome = empresa.getNome();
        this.categorias = empresa.getCategorias().stream().map(FornecedorCategoriaResponse::new).toList();
        this.category = this.categorias.isEmpty() ? "" : this.categorias.get(0).nome();
        this.contatos = empresa.getContatos().stream().map(FornecedorContatoResponse::new).toList();
        FornecedorContato principal = empresa.getContatos().isEmpty() ? null : empresa.getContatos().get(0);
        FornecedorContato secundario = empresa.getContatos().size() > 1 ? empresa.getContatos().get(1) : null;
        this.phone = principal != null ? principal.getTelefone() : null;
        this.email = principal != null ? principal.getEmail() : null;
        this.phone2 = secundario != null ? secundario.getTelefone() : null;
        this.email2 = secundario != null ? secundario.getEmail() : null;
        this.city = empresa.getCidade();
        this.cidade = empresa.getCidade();
        this.origem = empresa.getOrigem();
        this.estado = empresa.getEstado();
        this.projetosVinculados = empresa.getProjetos().stream().map(FornecedorProjetoResponse::new).toList();
        this.projects = this.projetosVinculados.stream()
                .map(FornecedorProjetoResponse::projetoCodigo)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
        this.jaTrabalhei = empresa.getJaTrabalhei() != null
                ? empresa.getJaTrabalhei()
                : !this.projetosVinculados.isEmpty();
        this.note = empresa.getObservacao();
        this.observacao = empresa.getObservacao();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getNome() { return nome; }
    public String getCategory() { return category; }
    public String getPhone() { return phone; }
    public String getPhone2() { return phone2; }
    public String getEmail() { return email; }
    public String getEmail2() { return email2; }
    public String getCity() { return city; }
    public String getCidade() { return cidade; }
    public String getOrigem() { return origem; }
    public String getEstado() { return estado; }
    public Boolean getJaTrabalhei() { return jaTrabalhei; }
    public String getProjects() { return projects; }
    public String getNote() { return note; }
    public String getObservacao() { return observacao; }
    public List<FornecedorCategoriaResponse> getCategorias() { return categorias; }
    public List<FornecedorContatoResponse> getContatos() { return contatos; }
    public List<FornecedorProjetoResponse> getProjetosVinculados() { return projetosVinculados; }
}
