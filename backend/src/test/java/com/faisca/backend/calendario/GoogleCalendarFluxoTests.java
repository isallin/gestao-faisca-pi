package com.faisca.backend.calendario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fluxo completo da integração Google Calendar via HTTP (sem depender do Google de verdade):
 * login → status → connect → callback → CRUD de tarefa com sync no-op → desconectar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:calendar;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "google.client.id=client-id-de-teste.apps.googleusercontent.com",
        "google.client.secret=segredo-de-teste",
        "google.redirect.uri=http://localhost:8080/api/calendar/callback"
})
class GoogleCalendarFluxoTests {

    @Value("${local.server.port}") int port;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER).build();

    @Autowired EntityManager em;
    @Autowired JdbcTemplate jdbc;

    private HttpResponse<String> send(String method, String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (json != null) builder.header("Content-Type", "application/json");
        return http.send(builder.method(method, json == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void fluxoCompletoDoCalendar() throws Exception {
        jdbc.execute("INSERT INTO fase (nome, ordem) VALUES ('Executivo', 1)");
        jdbc.execute("INSERT INTO tarefa_categoria (nome) VALUES ('Projeto')");

        // usuário + login
        assertEquals(201, send("POST", "/api/usuarios",
                "{\"nome\":\"Ana\",\"sobrenome\":\"Silva\",\"email\":\"ana@empresa.com\",\"cargo\":\"Arquiteta\",\"senha\":\"senha12345\"}", null).statusCode());
        HttpResponse<String> login = send("POST", "/api/auth/login",
                "{\"email\":\"ana@empresa.com\",\"senha\":\"senha12345\"}", null);
        assertEquals(200, login.statusCode(), login.body());
        String token = mapper.readTree(login.body()).path("token").asText();
        assertFalse(token.isBlank());

        // status começa desconectado
        HttpResponse<String> status = send("GET", "/api/calendar/status", null, token);
        assertEquals(200, status.statusCode());
        assertFalse(mapper.readTree(status.body()).path("connected").asBoolean());

        // connect devolve a URL do Google com state assinado
        HttpResponse<String> connect = send("GET", "/api/calendar/connect", null, token);
        assertEquals(200, connect.statusCode(), connect.body());
        String url = mapper.readTree(connect.body()).path("url").asText();
        assertTrue(url.contains("accounts.google.com"), url);
        assertTrue(url.contains("state="), url);
        assertTrue(url.contains("prompt=consent"), url);
        String state = url.substring(url.indexOf("state=") + 6);
        if (state.contains("&")) state = state.substring(0, state.indexOf('&'));

        // callback com state válido: como o código é falso, o Google rejeita e volta ?google=error
        HttpResponse<String> callback = send("GET", "/api/calendar/callback?code=codigofalso&state=" + state, null, null);
        assertEquals(302, callback.statusCode());
        assertTrue(callback.headers().firstValue("Location").orElse("").endsWith("/tarefa?google=error"),
                callback.headers().firstValue("Location").orElse(""));

        // callback com state forjado também cai no erro
        HttpResponse<String> forjado = send("GET", "/api/calendar/callback?code=codigofalso&state=forjado", null, null);
        assertEquals(302, forjado.statusCode());
        assertTrue(forjado.headers().firstValue("Location").orElse("").endsWith("/tarefa?google=error"));

        // CRUD com Google desconectado: sync é no-op e nada quebra
        HttpResponse<String> cliente = send("POST", "/api/clientes",
                "{\"tipo\":\"PF\",\"nome\":\"João Silva\",\"cpf\":\"52998224725\",\"telefone\":\"(11) 98765-4321\","
                        + "\"email\":\"joao@email.com\",\"cep\":\"01310-100\",\"logradouro\":\"Avenida Paulista\","
                        + "\"numero\":\"100\",\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"pais\":\"Brasil\"}", token);
        assertEquals(201, cliente.statusCode(), cliente.body());
        int clienteId = mapper.readTree(cliente.body()).path("id").asInt();

        HttpResponse<String> projeto = send("POST", "/api/projetos",
                "{\"clienteId\":" + clienteId + ",\"codigo\":\"PRJ-001\",\"nome\":\"Reforma Central\","
                        + "\"dataInicio\":\"2026-11-01T00:00:00\",\"dataFim\":\"2026-12-01T00:00:00\"}", token);
        assertEquals(201, projeto.statusCode(), projeto.body());
        int projetoId = mapper.readTree(projeto.body()).path("id").asInt();
        assertTrue(projetoId > 0, "id do projeto: " + projeto.body());

        JsonNode categorias = mapper.readTree(send("GET", "/api/tarefas/categorias", null, token).body());
        JsonNode fases = mapper.readTree(send("GET", "/api/tarefas/fases", null, token).body());
        int categoriaId = categorias.get(0).path("id").asInt();
        int faseId = fases.get(0).path("id").asInt();

        HttpResponse<String> tarefa = send("POST", "/api/tarefas",
                "{\"projetoId\":" + projetoId + ",\"categoriaId\":" + categoriaId + ",\"fkTarefaFase\":" + faseId + ","
                        + "\"nome\":\"Revisar planta\",\"prioridade\":\"Moderada\","
                        + "\"dataInicioPrev\":\"2026-11-20T09:00:00\",\"dataFimPrev\":\"2026-11-22T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(201, tarefa.statusCode(), tarefa.body());
        int tarefaId = mapper.readTree(tarefa.body()).path("id").asInt();

        HttpResponse<String> statusPatch = send("PATCH", "/api/tarefas/" + tarefaId + "/status",
                "{\"status\":\"Concluído\"}", token);
        assertEquals(200, statusPatch.statusCode(), statusPatch.body());
        assertEquals(1L, ((Number) em.createNativeQuery("SELECT COUNT(*) FROM status WHERE fkTarefa = " + tarefaId)
                .getSingleResult()).longValue());

        HttpResponse<String> registro = send("POST", "/api/registros",
                "{\"tarefaId\":" + tarefaId + ",\"inicio\":\"2026-11-20T09:00:00\",\"fim\":\"2026-11-20T11:00:00\",\"descricao\":\"Reunião\"}", token);
        assertEquals(201, registro.statusCode(), registro.body());

        // sem conexão, nenhuma tabela de evento foi populada
        assertEquals(0L, ((Number) em.createNativeQuery("SELECT COUNT(*) FROM google_event").getSingleResult()).longValue());
        assertEquals(0L, ((Number) em.createNativeQuery("SELECT COUNT(*) FROM google_token").getSingleResult()).longValue());

        // desconectar responde 204 mesmo sem estar conectado
        assertEquals(204, send("DELETE", "/api/calendar/conexao", null, token).statusCode());

        // regressão: desconectar COM token salvo remove o vínculo (o delete derivado exige transação)
        int usuarioId = ((Number) em.createNativeQuery("SELECT id FROM usuario WHERE email = 'ana@empresa.com'")
                .getSingleResult()).intValue();
        jdbc.execute("INSERT INTO google_token (fkUsuario, access_token, refresh_token, expira_em) VALUES ("
                + usuarioId + ", 'acesso-falso', 'refresh-falso', " + (System.currentTimeMillis() + 3_600_000) + ")");
        assertEquals(1L, ((Number) em.createNativeQuery("SELECT COUNT(*) FROM google_token").getSingleResult()).longValue());
        // Abrir Tarefas consulta status e listagens; mesmo com token Google inválido,
        // essas consultas devem funcionar sem chamar a API externa.
        assertTrue(mapper.readTree(send("GET", "/api/calendar/status", null, token).body()).path("connected").asBoolean());
        assertEquals(200, send("GET", "/api/tarefas", null, token).statusCode());
        assertEquals(200, send("GET", "/api/fases", null, token).statusCode());
        assertEquals(200, send("GET", "/api/projetos", null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/calendar/conexao", null, token).statusCode());
        assertEquals(0L, ((Number) em.createNativeQuery("SELECT COUNT(*) FROM google_token").getSingleResult()).longValue());
    }
}
