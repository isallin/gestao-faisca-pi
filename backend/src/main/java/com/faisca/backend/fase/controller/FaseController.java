package com.faisca.backend.fase.controller;

import com.faisca.backend.fase.dto.FaseResponse;
import com.faisca.backend.fase.repository.FaseRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fases")
public class FaseController {

    private final FaseRepository repository;

    public FaseController(FaseRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<FaseResponse> listar() {
        return repository.findAllByOrderByOrdemAsc().stream().map(FaseResponse::new).toList();
    }
}
