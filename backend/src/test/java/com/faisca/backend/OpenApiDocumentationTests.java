package com.faisca.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:openapi;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OpenApiDocumentationTests {
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${local.server.port}") int port;
    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> request(String method, String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (json != null) builder.header("Content-Type", "application/json");
        return http.send(builder.method(method, json == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void swaggerUiAndSpecsExposeRealRoutesAndSecurity() throws Exception {
        HttpResponse<String> ui = get("/swagger-ui/index.html");
        assertEquals(200, ui.statusCode());
        assertTrue(ui.body().contains("Swagger UI"));

        HttpResponse<String> json = get("/v3/api-docs");
        assertEquals(200, json.statusCode());
        JsonNode spec = mapper.readTree(json.body());
        assertTrue(spec.path("openapi").asText().startsWith("3."));
        assertEquals("Gestão Faísca API", spec.path("info").path("title").asText());
        assertEquals("http", spec.path("components").path("securitySchemes").path("bearerAuth").path("type").asText());
        assertEquals("bearer", spec.path("components").path("securitySchemes").path("bearerAuth").path("scheme").asText());

        String[] paths = {
                "/api/auth/login", "/api/usuarios", "/api/usuarios/{id}/tarefas",
                "/api/clientes", "/api/clientes/{clienteId}/representantes", "/api/clientes/{clienteId}/enderecos",
                "/api/projetos", "/api/projetos/{id}/endereco", "/api/projetos/{id}/fornecedores", "/api/projetos/{id}/fases",
                "/api/tarefas", "/api/tarefas/categorias", "/api/tarefas/fases", "/api/tarefas/prioridades",
                "/api/tarefas/{tarefaId}/subtarefas", "/api/tarefas/{tarefaId}/colaboradores",
                "/api/prioridades", "/api/tarefas-fases",
                "/api/registros", "/api/fases", "/api/dashboard",
                "/api/fornecedores", "/api/fornecedores/categorias",
                "/api/fornecedores/{fornecedorId}/contatos", "/api/fornecedores/{fornecedorId}/projetos",
                "/api/calendar/status", "/api/calendar/connect", "/api/calendar/callback", "/api/calendar/conexao"
        };
        for (String path : paths) assertTrue(spec.path("paths").has(path), "Rota ausente no OpenAPI: " + path);

        int operationCount = 0;
        var pathEntries = spec.path("paths").fields();
        while (pathEntries.hasNext()) {
            var pathEntry = pathEntries.next();
            var operations = pathEntry.getValue().fields();
            while (operations.hasNext()) {
                var operation = operations.next();
                operationCount++;
                JsonNode doc = operation.getValue();
                assertFalse(doc.path("summary").asText().isBlank(), pathEntry.getKey() + " " + operation.getKey());
                assertFalse(doc.path("description").asText().isBlank(), pathEntry.getKey() + " " + operation.getKey());
                assertTrue(doc.path("responses").size() >= 1, pathEntry.getKey() + " " + operation.getKey());
                assertTrue(doc.path("tags").size() >= 1, pathEntry.getKey() + " " + operation.getKey());
            }
        }
        assertTrue(operationCount >= 80, "Esperava >= 80 operações com V8, encontrou " + operationCount);
        assertEquals("Autenticação", spec.path("paths").path("/api/auth/login").path("post").path("tags").get(0).asText());
        assertEquals("Clientes", spec.path("paths").path("/api/clientes").path("get").path("tags").get(0).asText());
        assertTrue(spec.path("paths").path("/api/clientes").path("get").path("security").get(0).has("bearerAuth"));
        assertTrue(spec.path("paths").path("/api/auth/login").path("post").path("security").isMissingNode()
                || spec.path("paths").path("/api/auth/login").path("post").path("security").isEmpty());
        assertTrue(spec.path("paths").path("/api/usuarios").path("post").path("security").isMissingNode()
                || spec.path("paths").path("/api/usuarios").path("post").path("security").isEmpty());
        assertTrue(spec.path("components").path("schemas").path("LoginRequest").path("properties").path("senha").has("format"));
        assertTrue(spec.path("paths").path("/api/fornecedores").path("post").path("responses").path("201")
                .path("content").path("*/*").path("schema").has("$ref"),
                spec.path("paths").path("/api/fornecedores").path("post").path("responses").path("201").toString());

        HttpResponse<String> yaml = get("/v3/api-docs.yaml");
        assertEquals(200, yaml.statusCode());
        assertTrue(yaml.body().contains("openapi:"));

        assertEquals(401, get("/api/fases").statusCode());
    }

    @Test
    void documentedCrudWorksWithLoginAndBearerToken() throws Exception {
        assertEquals(201, request("POST", "/api/usuarios",
                "{\"nome\":\"Ana\",\"sobrenome\":\"Silva\",\"email\":\"ana@empresa.com\",\"cargo\":\"Arquiteta\",\"senha\":\"senha12345\"}", null).statusCode());
        HttpResponse<String> login = request("POST", "/api/auth/login",
                "{\"email\":\"ana@empresa.com\",\"senha\":\"senha12345\"}", null);
        assertEquals(200, login.statusCode());
        String token = mapper.readTree(login.body()).path("token").asText();
        assertFalse(token.isBlank());

        HttpResponse<String> created = request("POST", "/api/fornecedores",
                "{\"name\":\"Materiais Central\",\"category\":\"Materiais\",\"phone\":\"(11) 98765-4321\",\"email\":\"contato@materiais.com\",\"city\":\"São Paulo\"}", token);
        assertEquals(201, created.statusCode(), created.body());
        int id = mapper.readTree(created.body()).path("id").asInt();
        assertEquals(200, request("GET", "/api/fornecedores/" + id, null, token).statusCode());
        assertEquals(200, request("PUT", "/api/fornecedores/" + id,
                "{\"name\":\"Materiais Atualizados\",\"category\":\"Materiais\",\"phone\":\"(11) 98765-4321\",\"email\":\"contato@materiais.com\",\"city\":\"São Paulo\"}", token).statusCode());
        assertEquals(204, request("DELETE", "/api/fornecedores/" + id, null, token).statusCode());
        assertEquals(404, request("GET", "/api/fornecedores/" + id, null, token).statusCode());
    }
}
