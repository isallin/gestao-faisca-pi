package com.faisca.backend.fase.controller;

import com.faisca.backend.fase.dto.FaseResponse;
import com.faisca.backend.fase.repository.FaseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fases")
@Tag(name = "Fases", description = "Consulta de fases ordenadas")
@SecurityRequirement(name = "bearerAuth")
public class FaseController {

    private final FaseRepository repository;

    public FaseController(FaseRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Listar fases", description = "Retorna fases ordenadas pelo campo ordem.")
    @ApiResponse(responseCode = "200", description = "Lista de fases", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FaseResponse.class))))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @Transactional(readOnly = true)
    public List<FaseResponse> listar() {
        return repository.findAllByOrderByOrdemAsc().stream().map(FaseResponse::new).toList();
    }
}
