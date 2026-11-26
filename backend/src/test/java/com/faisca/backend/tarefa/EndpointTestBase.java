package com.faisca.backend.tarefa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Base dos testes de endpoints de tarefas: sobe a aplicação inteira com H2
 * em memória e faz requisições HTTP reais, igual ao MigracaoV8FluxoTests.
 * Todas as classes herdam as mesmas anotações, então o contexto Spring (e o
 * banco) é compartilhado — cada teste cria os próprios dados com valores únicos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:tarefas;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public abstract class EndpointTestBase {

    @Value("${local.server.port}")
    int port;

    protected final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();
    private static final AtomicLong SEQ = new AtomicLong();

    /** Gera um sufixo único para não colidir com dados de outros testes. */
    protected static String unico(String prefixo) {
        return prefixo + "_" + Long.toHexString(SEQ.incrementAndGet()) + Long.toHexString(System.nanoTime());
    }

    protected HttpResponse<String> send(String method, String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (json != null) builder.header("Content-Type", "application/json");
        return http.send(builder.method(method, json == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
    }

    protected JsonNode json(HttpResponse<String> resp) throws Exception {
        return mapper.readTree(resp.body());
    }

    protected int idDe(HttpResponse<String> resp) throws Exception {
        return json(resp).path("id").asInt();
    }

    /** Login do usuário criado pelo bootstrap da aplicação. */
    protected String login() throws Exception {
        HttpResponse<String> resp = send("POST", "/api/auth/login",
                "{\"email\":\"teste@faisca.com\",\"senha\":\"123456\"}", null);
        assertEquals(200, resp.statusCode(), resp.body());
        return json(resp).path("token").asText();
    }

    // ---------------------------------------------------------------
    // Fixtures: entidades de apoio para conseguir criar tarefas
    // ---------------------------------------------------------------

    /** Gera um CNPJ sintático com dígitos verificadores válidos (aceito pelo serviço). */
    protected static String cnpjValido() {
        int[] d = new int[14];
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < 12; i++) d[i] = rnd.nextInt(10);
        d[12] = dvCnpj(d, 12);
        d[13] = dvCnpj(d, 13);
        StringBuilder sb = new StringBuilder();
        for (int x : d) sb.append(x);
        return sb.toString();
    }

    private static int dvCnpj(int[] d, int tamanho) {
        int soma = 0, peso = tamanho - 7;
        for (int i = 0; i < tamanho; i++) {
            soma += d[i] * peso;
            if (--peso < 2) peso = 9;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    protected int criarFase(String token) throws Exception {
        HttpResponse<String> r = send("POST", "/api/fases",
                "{\"nome\":\"Fase " + unico("f") + "\",\"ordem\":1}", token);
        assertEquals(201, r.statusCode(), r.body());
        return idDe(r);
    }

    protected int criarCategoria(String token) throws Exception {
        HttpResponse<String> r = send("POST", "/api/tarefas/categorias",
                "{\"nome\":\"Categoria " + unico("c") + "\"}", token);
        assertEquals(201, r.statusCode(), r.body());
        return idDe(r);
    }

    protected int criarCliente(String token) throws Exception {
        String cnpj = cnpjValido();
        HttpResponse<String> r = send("POST", "/api/clientes",
                "{\"tipo\":\"PJ\",\"nomeFantasia\":\"Studio " + unico("s") + "\",\"cnpj\":\"" + cnpj + "\","
                        + "\"telefone\":\"(11) 98765-4321\",\"email\":\"contato" + unico("e") + "@aurora.com\","
                        + "\"cep\":\"01310-100\",\"logradouro\":\"Av Paulista\",\"numero\":\"1000\","
                        + "\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"pais\":\"Brasil\"}",
                token);
        assertEquals(201, r.statusCode(), r.body());
        return idDe(r);
    }

    protected int criarProjeto(String token, int clienteId, Integer faseId) throws Exception {
        String corpo = "{\"clienteId\":" + clienteId + ",\"codigo\":\"PRJ-" + unico("p") + "\","
                + "\"nome\":\"Obra " + unico("o") + "\""
                + (faseId != null ? ",\"fkTarefaFase\":" + faseId : "")
                + ",\"cep\":\"01310-100\",\"logradouro\":\"Rua Oscar Freire\",\"numero\":\"250\","
                + "\"bairro\":\"Jardins\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"pais\":\"Brasil\"}";
        HttpResponse<String> r = send("POST", "/api/projetos", corpo, token);
        assertEquals(201, r.statusCode(), r.body());
        return idDe(r);
    }

    protected int primeiraPrioridade(String token) throws Exception {
        HttpResponse<String> r = send("GET", "/api/prioridades", null, token);
        assertEquals(200, r.statusCode(), r.body());
        return json(r).get(0).path("id").asInt();
    }

    protected int criarTarefa(String token, int projetoId, int categoriaId, int faseId) throws Exception {
        int prioridadeId = primeiraPrioridade(token);
        HttpResponse<String> r = send("POST", "/api/tarefas",
                "{\"projetoId\":" + projetoId + ",\"categoriaId\":" + categoriaId + ",\"fkTarefaFase\":" + faseId + ","
                        + "\"nome\":\"Tarefa " + unico("t") + "\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(201, r.statusCode(), r.body());
        return idDe(r);
    }

    protected int criarColaborador(String token) throws Exception {
        HttpResponse<String> r = send("POST", "/api/usuarios",
                "{\"nome\":\"Colab " + unico("n") + "\",\"sobrenome\":\"Teste\",\"email\":\"" + unico("colab") + "@faisca.com\","
                        + "\"cargo\":\"Arquiteto\",\"senha\":\"senha12345\"}", null);
        assertEquals(201, r.statusCode(), r.body());
        return idDe(r);
    }

    /** Cenário completo: [projetoId, categoriaId, faseId, tarefaId] */
    protected int[] criarCenario(String token) throws Exception {
        int clienteId = criarCliente(token);
        int faseId = criarFase(token);
        int projetoId = criarProjeto(token, clienteId, faseId);
        int categoriaId = criarCategoria(token);
        int tarefaId = criarTarefa(token, projetoId, categoriaId, faseId);
        return new int[]{projetoId, categoriaId, faseId, tarefaId};
    }
}
