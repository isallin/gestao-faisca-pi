package com.faisca.backend.tarefa;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/** Endpoints de /api/prioridades (prioridade das tarefas). */
class PrioridadeEndpointTests extends EndpointTestBase {

    @Test
    void listarPrioridadesRetorna200ComSeed() throws Exception {
        String token = login();
        HttpResponse<String> resp = send("GET", "/api/prioridades", null, token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertTrue(json(resp).size() >= 5, "Seed de prioridades deve estar populado");
    }

    @Test
    void criarEBuscarPrioridade() throws Exception {
        String token = login();
        HttpResponse<String> criar = send("POST", "/api/prioridades",
                "{\"nome\":\"Prioridade " + unico("p") + "\"}", token);
        assertEquals(201, criar.statusCode(), criar.body());
        int id = idDe(criar);

        HttpResponse<String> buscar = send("GET", "/api/prioridades/" + id, null, token);
        assertEquals(200, buscar.statusCode(), buscar.body());
        assertEquals(id, json(buscar).path("id").asInt());
    }

    @Test
    void criarPrioridadeSemNomeRetorna400() throws Exception {
        String token = login();
        HttpResponse<String> resp = send("POST", "/api/prioridades", "{}", token);
        assertEquals(400, resp.statusCode(), resp.body());
    }

    @Test
    void buscarPrioridadeInexistenteRetorna404() throws Exception {
        String token = login();
        assertEquals(404, send("GET", "/api/prioridades/999999", null, token).statusCode());
    }

    @Test
    void atualizarPrioridadeRetorna200() throws Exception {
        String token = login();
        HttpResponse<String> criar = send("POST", "/api/prioridades",
                "{\"nome\":\"Prioridade " + unico("p") + "\"}", token);
        int id = idDe(criar);
        HttpResponse<String> resp = send("PUT", "/api/prioridades/" + id,
                "{\"nome\":\"Prioridade atualizada\"}", token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertEquals("Prioridade atualizada", json(resp).path("nome").asText());
    }

    @Test
    void excluirPrioridadeLivreRetorna204() throws Exception {
        String token = login();
        HttpResponse<String> criar = send("POST", "/api/prioridades",
                "{\"nome\":\"Prioridade " + unico("p") + "\"}", token);
        int id = idDe(criar);
        assertEquals(204, send("DELETE", "/api/prioridades/" + id, null, token).statusCode());
    }

    @Test
    void excluirPrioridadeVinculadaRetorna409() throws Exception {
        String token = login();
        HttpResponse<String> criar = send("POST", "/api/prioridades",
                "{\"nome\":\"Prioridade " + unico("p") + "\"}", token);
        int prioridadeId = idDe(criar);

        int[] cenario = criarCenario(token);
        HttpResponse<String> tarefa = send("POST", "/api/tarefas",
                "{\"projetoId\":" + cenario[0] + ",\"categoriaId\":" + cenario[1] + ",\"fkTarefaFase\":" + cenario[2] + ","
                        + "\"nome\":\"Tarefa com prioridade\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(201, tarefa.statusCode(), tarefa.body());
        int tarefaId = idDe(tarefa);

        assertEquals(409, send("DELETE", "/api/prioridades/" + prioridadeId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/tarefas/" + tarefaId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/prioridades/" + prioridadeId, null, token).statusCode());
    }

    @Test
    void semTokenRetorna401() throws Exception {
        assertEquals(401, send("GET", "/api/prioridades", null, null).statusCode());
    }
}
