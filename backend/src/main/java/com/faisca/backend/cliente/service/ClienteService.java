package com.faisca.backend.cliente.service;

import com.faisca.backend.cliente.dto.*;
import com.faisca.backend.cliente.entity.*;
import com.faisca.backend.cliente.repository.ClienteRepository;
import com.faisca.backend.cliente.repository.EnderecoClienteRepository;
import com.faisca.backend.cliente.repository.RepresentanteLegalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository repository;
    private final EnderecoClienteRepository enderecos;
    private final RepresentanteLegalRepository representantes;

    public ClienteService(ClienteRepository repository,
                          EnderecoClienteRepository enderecos,
                          RepresentanteLegalRepository representantes) {
        this.repository = repository;
        this.enderecos = enderecos;
        this.representantes = representantes;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(this::converter).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Integer id) {
        return converter(obter(id));
    }

    public ClienteResponse criar(ClienteCadastroRequest request) {
        Cliente cliente = new Cliente();
        preencher(cliente, request);
        return converter(repository.save(cliente));
    }

    public ClienteResponse atualizar(Integer id, ClienteCadastroRequest request) {
        Cliente cliente = obter(id);
        if (!cliente.getTipo().equals(request.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é permitido alterar o tipo do cliente");
        }
        preencher(cliente, request);
        return converter(repository.save(cliente));
    }

    public void excluir(Integer id) {
        Cliente cliente = obter(id);
        if (!cliente.getProjetos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cliente possui projetos");
        }
        repository.delete(cliente);
    }

    // -------- Representantes Legais (representante_legal V8) --------

    @Transactional(readOnly = true)
    public List<RepresentanteLegalResponse> listarRepresentantes(Integer clienteId) {
        ClientePj pj = obterClientePj(clienteId);
        return representantes.findByClientePjIdOrderByIdAsc(pj.getId()).stream()
                .map(RepresentanteLegalResponse::new)
                .toList();
    }

    public RepresentanteLegalResponse criarRepresentante(Integer clienteId, RepresentanteLegalRequest r) {
        ClientePj pj = obterClientePj(clienteId);
        RepresentanteLegal rep = new RepresentanteLegal();
        rep.setClientePj(pj);
        aplicarDadosRepresentante(rep, r);
        return new RepresentanteLegalResponse(representantes.save(rep));
    }

    public RepresentanteLegalResponse atualizarRepresentante(Integer clienteId, Integer representanteId, RepresentanteLegalRequest r) {
        ClientePj pj = obterClientePj(clienteId);
        RepresentanteLegal rep = representantes.findByIdAndClientePjId(representanteId, pj.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante legal não encontrado"));
        aplicarDadosRepresentante(rep, r);
        return new RepresentanteLegalResponse(representantes.save(rep));
    }

    public void excluirRepresentante(Integer clienteId, Integer representanteId) {
        ClientePj pj = obterClientePj(clienteId);
        RepresentanteLegal rep = representantes.findByIdAndClientePjId(representanteId, pj.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante legal não encontrado"));
        pj.getRepresentantes().removeIf(item -> item.getId().equals(representanteId));
        representantes.delete(rep);
    }

    // -------- Endereços do Cliente (endereco_cliente V8) --------

    @Transactional(readOnly = true)
    public List<EnderecoClienteResponse> listarEnderecos(Integer clienteId) {
        obter(clienteId);
        return enderecos.findByClienteIdOrderByIdAsc(clienteId).stream()
                .map(EnderecoClienteResponse::new)
                .toList();
    }

    public EnderecoClienteResponse criarEndereco(Integer clienteId, EnderecoClienteRequest r) {
        Cliente cliente = obter(clienteId);
        EnderecoCliente e = new EnderecoCliente();
        e.setCliente(cliente);
        aplicarDadosEndereco(e, r);
        EnderecoCliente salvo = enderecos.save(e);
        if (cliente.getLogradouro() == null || cliente.getLogradouro().isBlank()) {
            sincronizarEnderecoNoCliente(cliente, salvo);
        }
        return new EnderecoClienteResponse(salvo);
    }

    public EnderecoClienteResponse atualizarEndereco(Integer clienteId, Integer enderecoId, EnderecoClienteRequest r) {
        Cliente cliente = obter(clienteId);
        EnderecoCliente e = enderecos.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço do cliente não encontrado"));
        aplicarDadosEndereco(e, r);
        EnderecoCliente salvo = enderecos.save(e);
        if (!cliente.getEnderecos().isEmpty() && cliente.getEnderecos().get(0).getId().equals(salvo.getId())) {
            sincronizarEnderecoNoCliente(cliente, salvo);
        }
        return new EnderecoClienteResponse(salvo);
    }

    public void excluirEndereco(Integer clienteId, Integer enderecoId) {
        Cliente cliente = obter(clienteId);
        EnderecoCliente e = enderecos.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço do cliente não encontrado"));
        cliente.getEnderecos().removeIf(item -> item.getId().equals(enderecoId));
        enderecos.delete(e);
        if (!cliente.getEnderecos().isEmpty()) {
            sincronizarEnderecoNoCliente(cliente, cliente.getEnderecos().get(0));
        } else {
            limparEnderecoNoCliente(cliente);
        }
    }

    // -------- Auxiliares --------

    private Cliente obter(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private ClientePj obterClientePj(Integer clienteId) {
        Cliente cliente = obter(clienteId);
        if (cliente.getPessoaJuridica() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Representantes legais são permitidos apenas para clientes PJ");
        }
        return cliente.getPessoaJuridica();
    }

    private void aplicarDadosRepresentante(RepresentanteLegal rep, RepresentanteLegalRequest r) {
        rep.setNomeCompleto(r.getNomeCompleto().trim());
        rep.setCpf(digits(r.getCpf()));
        rep.setCargo(blank(r.getCargo()) ? null : r.getCargo().trim());
        rep.setEmail(blank(r.getEmail()) ? null : r.getEmail().trim().toLowerCase());
        rep.setTelefone(digits(r.getTelefone()));
    }

    private void aplicarDadosEndereco(EnderecoCliente e, EnderecoClienteRequest r) {
        e.setCep(digits(r.getCep()));
        e.setLogradouro(r.getLogradouro().trim());
        e.setNumero(r.getNumero().trim());
        e.setComplemento(blank(r.getComplemento()) ? null : r.getComplemento().trim());
        e.setBairro(r.getBairro().trim());
        e.setCidade(r.getCidade().trim());
        e.setEstado(r.getEstado().trim().toUpperCase());
        e.setPais(blank(r.getPais()) ? "Brasil" : r.getPais().trim());
    }

    private void sincronizarEnderecoNoCliente(Cliente c, EnderecoCliente e) {
        c.setCep(e.getCep());
        c.setLogradouro(e.getLogradouro());
        c.setNumero(e.getNumero());
        c.setComplemento(e.getComplemento());
        c.setBairro(e.getBairro());
        c.setCidade(e.getCidade());
        c.setEstado(e.getEstado());
        c.setPais(e.getPais());
    }

    private void limparEnderecoNoCliente(Cliente c) {
        c.setCep(null);
        c.setLogradouro(null);
        c.setNumero(null);
        c.setComplemento(null);
        c.setBairro(null);
        c.setCidade(null);
        c.setEstado(null);
        c.setPais(null);
    }

    private void preencher(Cliente c, ClienteCadastroRequest r) {
        boolean pf = "PF".equals(r.getTipo());
        String documento = digits(pf ? r.getCpf() : r.getCnpj());
        if (documento == null || documento.isBlank() || (pf ? blank(r.getNome()) : blank(r.getNomeFantasia()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e documento são obrigatórios");
        }
        if (pf ? repository.existsByPessoaFisicaCpfAndIdNot(documento, c.getId() == null ? -1 : c.getId())
               : repository.existsByPessoaJuridicaCnpjAndIdNot(documento, c.getId() == null ? -1 : c.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Documento já cadastrado");
        }
        c.setTipo(r.getTipo());
        c.setEmail(r.getEmail().trim().toLowerCase());
        c.setTelefone(digits(r.getTelefone()));
        c.setObservacao(r.getObservacao());

        if (pf) {
            ClientePf pessoa = c.getPessoaFisica();
            if (pessoa == null) { pessoa = new ClientePf(); c.setPessoaFisica(pessoa); }
            c.setNome(r.getNome().trim());
            pessoa.setCpf(documento);
            pessoa.setRg(r.getRg());
            pessoa.setDataNascimento(r.getDataNascimento());
            pessoa.setEstadoCivil(r.getEstadoCivil());
            pessoa.setProfissao(r.getProfissao());
        } else {
            ClientePj pessoa = c.getPessoaJuridica();
            if (pessoa == null) { pessoa = new ClientePj(); c.setPessoaJuridica(pessoa); }
            c.setNome(r.getNomeFantasia().trim());
            pessoa.setNomeFantasia(r.getNomeFantasia().trim());
            pessoa.setCnpj(documento);
            pessoa.getRepresentantes().clear();
            if (r.getRepresentantes() != null) {
                for (RepresentanteLegalRequest rep : r.getRepresentantes()) {
                    RepresentanteLegal representante = new RepresentanteLegal();
                    aplicarDadosRepresentante(representante, rep);
                    pessoa.addRepresentante(representante);
                }
            }
        }

        EnderecoCliente e;
        if (c.getEnderecos().isEmpty()) { e = new EnderecoCliente(); c.addEndereco(e); }
        else e = c.getEnderecos().get(0);
        e.setCep(digits(r.getCep()));
        e.setLogradouro(r.getLogradouro().trim());
        e.setNumero(r.getNumero().trim());
        e.setComplemento(r.getComplemento());
        e.setBairro(r.getBairro().trim());
        e.setCidade(r.getCidade().trim());
        e.setEstado(r.getEstado().toUpperCase());
        e.setPais(blank(r.getPais()) ? "Brasil" : r.getPais().trim());
        sincronizarEnderecoNoCliente(c, e);
    }

    private ClienteResponse converter(Cliente c) {
        EnderecoCliente e = c.getEnderecos().isEmpty() ? null : c.getEnderecos().get(0);
        String logradouro = e != null ? e.getLogradouro() : c.getLogradouro();
        String numero = e != null ? e.getNumero() : c.getNumero();
        String complemento = e != null ? e.getComplemento() : c.getComplemento();
        String bairro = e != null ? e.getBairro() : c.getBairro();
        String cidade = e != null ? e.getCidade() : c.getCidade();
        String estado = e != null ? e.getEstado() : c.getEstado();
        String cep = e != null ? e.getCep() : c.getCep();
        String pais = e != null ? e.getPais() : c.getPais();

        String endereco = blank(logradouro) ? "" : logradouro + ", " + (numero == null ? "" : numero)
                + (blank(complemento) ? "" : " - " + complemento)
                + " — " + (bairro == null ? "" : bairro) + ", " + (cidade == null ? "" : cidade) + "-" + (estado == null ? "" : estado)
                + (cep != null && cep.length() == 8 ? ", " + cep.substring(0, 5) + "-" + cep.substring(5) : "");

        String projetos = c.getProjetos().stream().map(p -> p.getCodigo()).collect(Collectors.joining(", "));
        ClienteResponse response = new ClienteResponse(
                c.getId(),
                c.getNome(),
                "PF".equals(c.getTipo()) ? "Pessoa Física" : "Pessoa Jurídica",
                projetos,
                endereco,
                formatarTelefone(c.getTelefone()),
                c.getEmail(),
                c.getObservacao() == null ? "" : c.getObservacao()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("phone", response.getPhone());
        details.put("email", c.getEmail());
        details.put("note", c.getObservacao());
        if (!blank(logradouro) || !blank(cep)) {
            details.put("zip", cep);
            details.put("street", logradouro);
            details.put("number", numero);
            details.put("complement", complemento);
            details.put("neighborhood", bairro);
            details.put("city", cidade);
            details.put("state", estado);
            details.put("country", pais);
        }
        if (c.getPessoaFisica() != null) {
            ClientePf pf = c.getPessoaFisica();
            String[] nome = c.getNome().split(" ", 2);
            details.put("name", nome[0]);
            details.put("last", nome.length > 1 ? nome[1] : "");
            details.put("cpf", pf.getCpf());
            details.put("rg", pf.getRg());
            details.put("birth", pf.getDataNascimento());
            details.put("marital", pf.getEstadoCivil());
            details.put("job", pf.getProfissao());
        }
        if (c.getPessoaJuridica() != null) {
            ClientePj pj = c.getPessoaJuridica();
            details.put("company", pj.getNomeFantasia());
            details.put("cnpj", pj.getCnpj());
            if (!pj.getRepresentantes().isEmpty()) {
                RepresentanteLegal rep = pj.getRepresentantes().get(0);
                details.put("repName", rep.getNomeCompleto());
                details.put("repCpf", rep.getCpf());
                details.put("repRole", rep.getCargo());
                details.put("repEmail", rep.getEmail());
                details.put("repPhone", rep.getTelefone());
            }
            response.setRepresentantes(pj.getRepresentantes().stream().map(RepresentanteLegalResponse::new).toList());
        } else {
            response.setRepresentantes(List.of());
        }
        response.setEnderecos(c.getEnderecos().stream().map(EnderecoClienteResponse::new).toList());
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
