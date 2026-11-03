package com.faisca.backend.dashboard.controller;

import com.faisca.backend.dashboard.dto.DashboardResponse;
import com.faisca.backend.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Indicadores de tarefas, horas e urgências")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar dashboard", description = "Retorna KPIs e tarefas urgentes, com filtro opcional pelo nome do projeto.")
    @ApiResponse(responseCode = "200", description = "Indicadores calculados", content = @Content(schema = @Schema(implementation = DashboardResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    public DashboardResponse listar(@Parameter(description = "Nome do projeto para filtrar os indicadores", example = "Reforma Central") @RequestParam(required = false) String projeto) {
        return service.listar(projeto);
    }
}
