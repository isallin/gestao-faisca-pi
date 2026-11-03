package com.faisca.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI gestaoFaiscaApi() {
        return new OpenAPI()
                .info(new Info().title("Gestão Faísca API")
                        .description("API de gestão de clientes, usuários, projetos, fases, tarefas, horas, fornecedores e dashboard. "
                                + "Faça login em /api/auth/login e informe o token recebido no botão Authorize.")
                        .version("0.0.1"))
                .schemaRequirement("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                        .description("Token JWT retornado pelo login"));
    }
}
