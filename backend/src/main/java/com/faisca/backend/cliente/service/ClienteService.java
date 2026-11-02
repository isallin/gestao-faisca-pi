package com.faisca.backend.cliente.service;

import com.faisca.backend.cliente.dto.*;
import com.faisca.backend.cliente.entity.*;
import com.faisca.backend.cliente.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {
    private final ClienteRepository repository;
    public ClienteService(ClienteRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() { return repository.findAll().stream().map(this::converter).toList(); }
    @Transactional(readOnly = true)
    public ClienteResponse buscar(Integer id) { return converter(obter(id)); }
    public ClienteResponse criar(ClienteCadastroRequest request) {
        Cliente cliente = new Cliente();
        preencher(cliente, request);
        return converter(repository.save(cliente));
    }
    public ClienteResponse atualizar(Integer id, ClienteCadastroRequest request) {
        Cliente cliente = obter(id);
        if (!cliente.getTipo().equals(request.getTipo()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido alterar o tipo do cliente");
        preencher(cliente, request);
        return converter(repository.save(cliente));
    }
    public void excluir(Integer id) {
        Cliente cliente = obter(id);
        if (!cliente.getProjetos().isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente possui projetos");
        repository.delete(cliente);
    }
    private Cliente obter(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }
    private void preencher(Cliente c, ClienteCadastroRequest r) {
        boolean pf = "PF".equals(r.getTipo());
        String documento = digits(pf ? r.getCpf() : r.getCnpj());
        if (documento == null || documento.isBlank() || (pf ? blank(r.getNome()) : blank(r.getNomeFantasia())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e documento são obrigatórios");
        if (pf ? repository.existsByPessoaFisicaCpfAndIdNot(documento, c.getId() == null ? -1 : c.getId())
               : repository.existsByPessoaJuridicaCnpjAndIdNot(documento, c.getId() == null ? -1 : c.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Documento já cadastrado");
        c.setTipo(r.getTipo()); c.setEmail(r.getEmail().trim().toLowerCase());
        c.setTelefone(digits(r.getTelefone())); c.setObservacao(r.getObservacao());
        if (pf) {
            ClientePf pessoa = c.getPessoaFisica();
            if (pessoa == null) { pessoa = new ClientePf(); c.setPessoaFisica(pessoa); }
            c.setNome(r.getNome().trim()); pessoa.setCpf(documento); pessoa.setRg(r.getRg());
            pessoa.setDataNascimento(r.getDataNascimento()); pessoa.setEstadoCivil(r.getEstadoCivil()); pessoa.setProfissao(r.getProfissao());
        } else {
            ClientePj pessoa = c.getPessoaJuridica();
            if (pessoa == null) { pessoa = new ClientePj(); c.setPessoaJuridica(pessoa); }
            c.setNome(r.getNomeFantasia().trim()); pessoa.setNomeFantasia(r.getNomeFantasia().trim()); pessoa.setCnpj(documento);
            pessoa.getRepresentantes().clear();
            if (r.getRepresentantes() != null) for (RepresentanteLegalRequest rep : r.getRepresentantes()) {
                RepresentanteLegal representante = new RepresentanteLegal();
                representante.setNomeCompleto(rep.getNomeCompleto().trim()); representante.setCpf(digits(rep.getCpf()));
                representante.setCargo(rep.getCargo()); representante.setEmail(rep.getEmail()); representante.setTelefone(digits(rep.getTelefone()));
                pessoa.addRepresentante(representante);
            }
        }
        EnderecoCliente e;
        if (c.getEnderecos().isEmpty()) { e = new EnderecoCliente(); c.addEndereco(e); }
        else e = c.getEnderecos().get(0);
        e.setCep(digits(r.getCep())); e.setLogradouro(r.getLogradouro().trim()); e.setNumero(r.getNumero().trim());
        e.setComplemento(r.getComplemento()); e.setBairro(r.getBairro().trim()); e.setCidade(r.getCidade().trim());
        e.setEstado(r.getEstado().toUpperCase()); e.setPais(blank(r.getPais()) ? "Brasil" : r.getPais().trim());
    }
    private ClienteResponse converter(Cliente c) {
        EnderecoCliente e = c.getEnderecos().isEmpty() ? null : c.getEnderecos().get(0);
        String endereco = e == null ? "" : e.getLogradouro() + ", " + e.getNumero()
                + (blank(e.getComplemento()) ? "" : " - " + e.getComplemento())
                + " — " + e.getBairro() + ", " + e.getCidade() + "-" + e.getEstado()
                + (e.getCep() != null && e.getCep().length() == 8 ? ", " + e.getCep().substring(0, 5) + "-" + e.getCep().substring(5) : "");
        String projetos = c.getProjetos().stream().map(p -> p.getCodigo()).collect(Collectors.joining(", "));
        ClienteResponse response = new ClienteResponse(c.getId(), c.getNome(), "PF".equals(c.getTipo()) ? "Pessoa Física" : "Pessoa Jurídica",
                projetos, endereco, formatarTelefone(c.getTelefone()), c.getEmail(), c.getObservacao() == null ? "" : c.getObservacao());
        Map<String, Object> details = new HashMap<>();
        details.put("phone", response.getPhone()); details.put("email", c.getEmail()); details.put("note", c.getObservacao());
        if (e != null) {
            details.put("zip", e.getCep()); details.put("street", e.getLogradouro()); details.put("number", e.getNumero());
            details.put("complement", e.getComplemento()); details.put("neighborhood", e.getBairro());
            details.put("city", e.getCidade()); details.put("state", e.getEstado()); details.put("country", e.getPais());
        }
        if (c.getPessoaFisica() != null) {
            ClientePf pf = c.getPessoaFisica();
            String[] nome = c.getNome().split(" ", 2);
            details.put("name", nome[0]); details.put("last", nome.length > 1 ? nome[1] : "");
            details.put("cpf", pf.getCpf()); details.put("rg", pf.getRg()); details.put("birth", pf.getDataNascimento());
            details.put("marital", pf.getEstadoCivil()); details.put("job", pf.getProfissao());
        }
        if (c.getPessoaJuridica() != null) {
            ClientePj pj = c.getPessoaJuridica();
            details.put("company", pj.getNomeFantasia()); details.put("cnpj", pj.getCnpj());
            if (!pj.getRepresentantes().isEmpty()) {
                RepresentanteLegal rep = pj.getRepresentantes().get(0);
                details.put("repName", rep.getNomeCompleto()); details.put("repCpf", rep.getCpf());
                details.put("repRole", rep.getCargo()); details.put("repEmail", rep.getEmail()); details.put("repPhone", rep.getTelefone());
            }
        }
        response.setDetails(details);
        return response;
    }
    private String formatarTelefone(String t) {
        if (t == null) return "";
        if (t.length() == 11) return "(" + t.substring(0, 2) + ") " + t.substring(2, 7) + "-" + t.substring(7);
        if (t.length() == 10) return "(" + t.substring(0, 2) + ") " + t.substring(2, 6) + "-" + t.substring(6);
        return t;
    }
    private String digits(String s) { return s == null ? null : s.replaceAll("\\D", ""); }
    private boolean blank(String s) { return s == null || s.isBlank(); }
}
