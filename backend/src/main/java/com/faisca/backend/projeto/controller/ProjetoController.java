package com.faisca.backend.projeto.controller;

import com.faisca.backend.cliente.entity.Cliente;
import com.faisca.backend.cliente.repository.ClienteRepository;
import com.faisca.backend.projeto.entity.Projeto;
import com.faisca.backend.projeto.repository.ProjetoRepository;
import com.faisca.backend.projeto.dto.ProjetoCadastroRequest;
import com.faisca.backend.projeto.dto.ProjetoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoRepository projetoRepository;
    private final ClienteRepository clienteRepository;

    public ProjetoController(ProjetoRepository projetoRepository,
                             ClienteRepository clienteRepository) {
        this.projetoRepository = projetoRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ProjetoResponse> listar() {
        return projetoRepository.findAllByOrderByCodigoAsc().stream().map(ProjetoResponse::new).toList();
    }

    @PostMapping
    public ResponseEntity<ProjetoResponse> cadastrar(
            @RequestBody @Valid ProjetoCadastroRequest request) {

        if (request.getDataInicio() != null && request.getDataFim() != null
                && request.getDataFim().isBefore(request.getDataInicio())) {
            return ResponseEntity.status(400).build();
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId()).orElse(null);

        if (cliente == null) {
            return ResponseEntity.status(404).build();
        }

        Projeto projeto = new Projeto();
        projeto.setCliente(cliente);
        projeto.setCodigo(tratarTexto(request.getCodigo()));
        projeto.setRelacaoProprietario(tratarTexto(request.getRelacaoProprietario()));
        projeto.setNome(tratarTexto(request.getNome()));
        projeto.setTipoImovel(tratarTexto(request.getTipoImovel()));
        projeto.setAreaM2(request.getAreaM2());
        projeto.setUnidadeNumero(tratarTexto(request.getUnidadeNumero()));
        projeto.setCondominio(tratarTexto(request.getCondominio()));
        projeto.setMatricula(tratarTexto(request.getMatricula()));
        projeto.setDataInicio(request.getDataInicio());
        projeto.setDataFim(request.getDataFim());
        projeto.setObservacao(tratarTexto(request.getObservacao()));
        projeto.setFkTarefaFase(request.getFkTarefaFase());

        Projeto registro = projetoRepository.save(projeto);

        return ResponseEntity.status(201).body(new ProjetoResponse(registro));
    }

    private String tratarTexto(String valor) {
        return valor == null ? null : valor.trim();
    }
}
