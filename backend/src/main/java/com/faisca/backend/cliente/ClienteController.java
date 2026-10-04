package com.faisca.backend.cliente;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ClienteController {
    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    // Página "clients": lista todos os clientes -- Gabriel Medeiros
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<ClienteResponse>> listar() {
        List<Cliente> clientes = repository.findAll();
        List<ClienteResponse> res = new ArrayList<>();

        for (int i = 0; i < clientes.size(); i++) {
            res.add(converterParaResponse(clientes.get(i)));
        }

        return ResponseEntity.status(200).body(res);
    }

    // Página "client-create": cadastra cliente PF ou PJ -- Gabriel Medeiros
    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteCadastroRequest request) {
        boolean pessoaFisica = request.getTipo().equals("PF");
        String cpf = soDigitos(request.getCpf());
        String cnpj = soDigitos(request.getCnpj());

        if (pessoaFisica) {
            if (vazio(request.getNome()) || vazio(cpf)) {
                return ResponseEntity.status(400).build();
            }
            if (repository.existsByPessoaFisicaCpf(cpf)) {
                return ResponseEntity.status(409).build();
            }
        } else {
            if (vazio(request.getNomeFantasia()) || vazio(cnpj)) {
                return ResponseEntity.status(400).build();
            }
            if (repository.existsByPessoaJuridicaCnpj(cnpj)) {
                return ResponseEntity.status(409).build();
            }
        }

        Cliente cliente = new Cliente();
        cliente.setTipo(request.getTipo());
        cliente.setEmail(request.getEmail().trim().toLowerCase());
        cliente.setTelefone(soDigitos(request.getTelefone()));
        cliente.setObservacao(request.getObservacao());

        if (pessoaFisica) {
            ClientePf pf = new ClientePf();
            pf.setNome(request.getNome().trim());
            pf.setCpf(cpf);
            pf.setRg(request.getRg());
            pf.setDataNascimento(request.getDataNascimento());
            pf.setEstadoCivil(request.getEstadoCivil());
            pf.setProfissao(request.getProfissao());
            cliente.setPessoaFisica(pf);
        } else {
            ClientePj pj = new ClientePj();
            pj.setCnpj(cnpj);
            pj.setNomeFantasia(request.getNomeFantasia().trim());

            if (request.getRepresentantes() != null) {
                for (int i = 0; i < request.getRepresentantes().size(); i++) {
                    RepresentanteLegalRequest r = request.getRepresentantes().get(i);

                    RepresentanteLegal representante = new RepresentanteLegal();
                    representante.setNomeCompleto(r.getNomeCompleto().trim());
                    representante.setCpf(soDigitos(r.getCpf()));
                    representante.setCargo(r.getCargo());
                    representante.setEmail(r.getEmail());
                    representante.setTelefone(soDigitos(r.getTelefone()));
                    pj.addRepresentante(representante);
                }
            }
            cliente.setPessoaJuridica(pj);
        }

        EnderecoCliente endereco = new EnderecoCliente();
        endereco.setCep(soDigitos(request.getCep()));
        endereco.setLogradouro(request.getLogradouro().trim());
        endereco.setNumero(request.getNumero().trim());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro().trim());
        endereco.setCidade(request.getCidade().trim());
        endereco.setEstado(request.getEstado().toUpperCase());
        endereco.setPais(vazio(request.getPais()) ? "Brasil" : request.getPais().trim());
        cliente.addEndereco(endereco);

        Cliente registro = repository.save(cliente);

        return ResponseEntity.status(201).body(converterParaResponse(registro));
    }

    // Métodos auxiliares -- Gabriel Medeiros
    private ClienteResponse converterParaResponse(Cliente c) {
        String nome = "";
        String tipo = "Pessoa Jurídica";

        if (c.getTipo().equals("PF")) {
            tipo = "Pessoa Física";
            if (c.getPessoaFisica() != null) {
                nome = c.getPessoaFisica().getNome();
            }
        } else if (c.getPessoaJuridica() != null) {
            nome = c.getPessoaJuridica().getNomeFantasia();
        }

        String projetos = "";
        for (int i = 0; i < c.getProjetos().size(); i++) {
            if (i > 0) {
                projetos += ", ";
            }
            projetos += c.getProjetos().get(i).getCodigo();
        }

        String endereco = "";
        if (!c.getEnderecos().isEmpty()) {
            EnderecoCliente e = c.getEnderecos().get(0);

            endereco = e.getLogradouro() + ", " + e.getNumero();
            if (!vazio(e.getComplemento())) {
                endereco += " - " + e.getComplemento();
            }
            endereco += " — " + e.getBairro() + ", " + e.getCidade() + "-" + e.getEstado();
            if (e.getCep() != null && e.getCep().length() == 8) {
                endereco += ", " + e.getCep().substring(0, 5) + "-" + e.getCep().substring(5);
            }
        }

        return new ClienteResponse(
                c.getId(),
                nome,
                tipo,
                projetos,
                endereco,
                formatarTelefone(c.getTelefone()),
                c.getEmail(),
                c.getObservacao() == null ? "" : c.getObservacao()
        );
    }

    private String formatarTelefone(String t) {
        if (t == null) {
            return "";
        }
        if (t.length() == 11) {
            return "(" + t.substring(0, 2) + ") " + t.substring(2, 7) + "-" + t.substring(7);
        }
        if (t.length() == 10) {
            return "(" + t.substring(0, 2) + ") " + t.substring(2, 6) + "-" + t.substring(6);
        }
        return t;
    }

    private String soDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
