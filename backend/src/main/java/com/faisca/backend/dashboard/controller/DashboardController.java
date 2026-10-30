package com.faisca.backend.dashboard.controller;

import com.faisca.backend.dashboard.dto.DashboardResponse;
import com.faisca.backend.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public DashboardResponse listar(@RequestParam(required = false) String projeto) {
        return service.listar(projeto);
    }
}
