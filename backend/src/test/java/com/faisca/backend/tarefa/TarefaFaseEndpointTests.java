package com.faisca.backend.tarefa;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/** Endpoints de /api/tarefas-fases (associação projeto-fase-tarefa). */
class TarefaFaseEndpointTests extends EndpointTestBase {

    @Test
    void listarAssociacoesRetorna200() throws Exception {
        String token = login();
        HttpResponse<String> resp = send("GET", "/api/tarefas-fases", null, token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertTrue(json(resp).isArray());
    }

    @Test
    void listarComFiltrosRetorna200() throws Exception {
        String token = login();
        int[] cenario = criarCenario(token);
        for (String filtro : new String[]{"projetoId=" + cenario[0], "faseId=" + cenario[2], "tarefaId=" + cenario[3]}) {
            HttpResponse<String> resp = send("GET", "/api/tarefas-fases?" + filtro, null, token);
            assertEquals(200, resp.statusCode(), filtro + " -> " + resp.body());
            assertTrue(json(resp).isArray());
        }
    }

    @Test
    void criarAssociacaoRetorna201() throws Exception {
        String token = login();
        int[] cenario = criarCenario(token);
        HttpResponse<String> resp = send("POST", "/api/tarefas-fases",
                "{\"projetoId\":" + cenario[0] + ",\"faseId\":" + cenario[2] + ",\"tarefaId\":" + cenario[3] + "}",
                token);
        assertEquals(201, resp.statusCode(), resp.body());
    }

    @Test
    void criarAssociacaoComTarefaInexistenteRetorna404() throws Exception {
        String token = login();
        int clienteId = criarCliente(token);
        int faseId = criarFase(token);
        int projetoId = criarProjeto(token, clienteId, faseId);
        HttpResponse<String> resp = send("POST", "/api/tarefas-fases",
                "{\"projetoId\":" + projetoId + ",\"faseId\":" + faseId + ",\"tarefaId\":999999}", token);
        assertEquals(404, resp.statusCode(), resp.body());
    }

    @Test
    void excluirAssociacaoRetorna204() throws Exception {
        String token = login();
        int[] cenario = criarCenario(token);
        HttpResponse<String> criar = send("POST", "/api/tarefas-fases",
                "{\"projetoId\":" + cenario[0] + ",\"faseId\":" + cenario[2] + ",\"tarefaId\":" + cenario[3] + "}",
                token);
        assertEquals(201, criar.statusCode(), criar.body());

        assertEquals(204, send("DELETE", "/api/tarefas-fases/" + cenario[0] + "/" + cenario[2] + "/" + cenario[3],
                null, token).statusCode());
    }

    @Test
    void excluirAssociacaoInexistenteRetorna404() throws Exception {
        String token = login();
        assertEquals(404, send("DELETE", "/api/tarefas-fases/999999/999999/999999", null, token).statusCode());
    }

    @Test
    void semTokenRetorna401() throws Exception {
        assertEquals(401, send("GET", "/api/tarefas-fases", null, null).statusCode());
    }
}
