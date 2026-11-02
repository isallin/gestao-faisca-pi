package com.faisca.backend.fornecedor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {
    private final FornecedorRepository repository;
    public FornecedorController(FornecedorRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Fornecedor> listar() { return repository.findAll(); }
    @GetMapping("/{id}")
    public Fornecedor buscar(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));
    }
    @PostMapping
    public ResponseEntity<Fornecedor> criar(@Valid @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(fornecedor));
    }
    @PutMapping("/{id}")
    public Fornecedor atualizar(@PathVariable Integer id, @Valid @RequestBody Fornecedor dados) {
        Fornecedor fornecedor = buscar(id);
        fornecedor.setName(dados.getName()); fornecedor.setCategory(dados.getCategory());
        fornecedor.setPhone(dados.getPhone()); fornecedor.setPhone2(dados.getPhone2());
        fornecedor.setEmail(dados.getEmail()); fornecedor.setEmail2(dados.getEmail2());
        fornecedor.setCity(dados.getCity()); fornecedor.setProjects(dados.getProjects());
        fornecedor.setNote(dados.getNote());
        return repository.save(fornecedor);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        repository.delete(buscar(id));
        return ResponseEntity.noContent().build();
    }
}
