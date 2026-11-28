package com.faisca.backend.tarefa;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Endpoints de /api/tarefas (inclui categorias, listas auxiliares,
 * subtarefas e colaboradores).
 */
class TarefaEndpointTests extends EndpointTestBase {

    // ------------------------- tarefas CRUD -------------------------

    @Test
    void listarTarefasRetorna200() throws Exception {
        String token = login();
        HttpResponse<String> resp = send("GET", "/api/tarefas", null, token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertTrue(json(resp).isArray());
    }

    @Test
    void criarTarefaCompletaRetorna201() throws Exception {
        String token = login();
        int[] cenario = criarCenario(token);
        int prioridadeId = primeiraPrioridade(token);
        int colaboradorId = criarColaborador(token);

        HttpResponse<String> resp = send("POST", "/api/tarefas",
                "{\"projetoId\":" + cenario[0] + ",\"categoriaId\":" + cenario[1] + ",\"fkTarefaFase\":" + cenario[2] + ","
                        + "\"nome\":\"Detalhar iluminação " + unico("t") + "\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\",\"colaboradoresIds\":[" + colaboradorId + "],"
                        + "\"subtarefas\":[{\"nome\":\"Definir luminárias\",\"check\":false}]}", token);
        assertEquals(201, resp.statusCode(), resp.body());
        assertEquals(prioridadeId, json(resp).path("prioridadeId").asInt());
        assertEquals(1, json(resp).path("subtarefas").size());
    }

    @Test
    void buscarTarefaPorIdRetorna200() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];
        HttpResponse<String> resp = send("GET", "/api/tarefas/" + tarefaId, null, token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertEquals(tarefaId, json(resp).path("id").asInt());
    }

    @Test
    void buscarTarefaInexistenteRetorna404() throws Exception {
        String token = login();
        assertEquals(404, send("GET", "/api/tarefas/999999", null, token).statusCode());
    }

    @Test
    void atualizarTarefaRetorna200() throws Exception {
        String token = login();
        int[] cenario = criarCenario(token);
        int colaboradorId = criarColaborador(token);
        HttpResponse<String> resp = send("PUT", "/api/tarefas/" + cenario[3],
                "{\"projetoId\":" + cenario[0] + ",\"categoriaId\":" + cenario[1] + ",\"fkTarefaFase\":" + cenario[2] + ","
                        + "\"nome\":\"Tarefa atualizada\",\"prioridadeId\":" + primeiraPrioridade(token) + ","
                        + "\"colaboradorId\":" + colaboradorId + ","
                        + "\"dataInicioPrev\":\"2026-12-02T09:00:00\","
                        + "\"dataFimPrev\":\"2026-12-06T18:00:00\",\"status\":\"Concluído\"}", token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertEquals("Tarefa atualizada", json(resp).path("nome").asText());
    }

    @Test
    void atualizarStatusDaTarefaRetorna200() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];
        HttpResponse<String> resp = send("PATCH", "/api/tarefas/" + tarefaId + "/status",
                "{\"status\":\"Concluído\"}", token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertEquals("Concluído", json(resp).path("status").asText());
    }

    @Test
    void listarStatusDaTarefaRetorna200() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];
        HttpResponse<String> resp = send("GET", "/api/tarefas/" + tarefaId + "/status", null, token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertTrue(json(resp).isArray());
    }

    @Test
    void excluirTarefaRetorna204() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];
        assertEquals(204, send("DELETE", "/api/tarefas/" + tarefaId, null, token).statusCode());
    }

    @Test
    void criarTarefaSemNomeRetorna400() throws Exception {
        String token = login();
        HttpResponse<String> resp = send("POST", "/api/tarefas",
                "{\"projetoId\":1,\"categoriaId\":1,\"fkTarefaFase\":1,"
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(400, resp.statusCode(), resp.body());
    }

    @Test
    void criarEAtualizarTarefaSemResponsavelRetorna400() throws Exception {
        String token = login();
        int[] cenario = criarCenario(token);
        int prioridadeId = primeiraPrioridade(token);

        HttpResponse<String> semResponsavel = send("POST", "/api/tarefas",
                "{\"projetoId\":" + cenario[0] + ",\"categoriaId\":" + cenario[1] + ",\"fkTarefaFase\":" + cenario[2] + ","
                        + "\"nome\":\"Sem responsável\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(400, semResponsavel.statusCode(), semResponsavel.body());
        assertEquals("Responsável é obrigatório", json(semResponsavel).path("message").asText());

        HttpResponse<String> listaVazia = send("POST", "/api/tarefas",
                "{\"projetoId\":" + cenario[0] + ",\"categoriaId\":" + cenario[1] + ",\"fkTarefaFase\":" + cenario[2] + ","
                        + "\"nome\":\"Lista vazia\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"colaboradoresIds\":[],"
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(400, listaVazia.statusCode(), listaVazia.body());

        HttpResponse<String> atualizarSemResponsavel = send("PUT", "/api/tarefas/" + cenario[3],
                "{\"projetoId\":" + cenario[0] + ",\"categoriaId\":" + cenario[1] + ",\"fkTarefaFase\":" + cenario[2] + ","
                        + "\"nome\":\"Atualizar sem responsável\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"dataInicioPrev\":\"2026-12-01T09:00:00\",\"dataFimPrev\":\"2026-12-05T18:00:00\","
                        + "\"status\":\"Em andamento\"}", token);
        assertEquals(400, atualizarSemResponsavel.statusCode(), atualizarSemResponsavel.body());
    }

    // ---------------------- categorias de tarefa ----------------------

    @Test
    void crudCategoriaDeTarefa() throws Exception {
        String token = login();

        HttpResponse<String> listar = send("GET", "/api/tarefas/categorias", null, token);
        assertEquals(200, listar.statusCode(), listar.body());

        HttpResponse<String> criar = send("POST", "/api/tarefas/categorias",
                "{\"nome\":\"Categoria " + unico("c") + "\"}", token);
        assertEquals(201, criar.statusCode(), criar.body());
        int categoriaId = idDe(criar);

        HttpResponse<String> atualizar = send("PUT", "/api/tarefas/categorias/" + categoriaId,
                "{\"nome\":\"Categoria atualizada\"}", token);
        assertEquals(200, atualizar.statusCode(), atualizar.body());
        assertEquals("Categoria atualizada", json(atualizar).path("nome").asText());

        assertEquals(204, send("DELETE", "/api/tarefas/categorias/" + categoriaId, null, token).statusCode());
    }

    // ----------------------- listas auxiliares -----------------------

    @Test
    void listarFasesEPrioridadesRetornam200() throws Exception {
        String token = login();
        HttpResponse<String> fases = send("GET", "/api/tarefas/fases", null, token);
        assertEquals(200, fases.statusCode(), fases.body());
        HttpResponse<String> prioridades = send("GET", "/api/tarefas/prioridades", null, token);
        assertEquals(200, prioridades.statusCode(), prioridades.body());
        assertTrue(json(prioridades).size() >= 5, "Seed de prioridades deve estar populado");
    }

    // -------------------------- subtarefas ---------------------------

    @Test
    void crudSubtarefa() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];

        HttpResponse<String> criar = send("POST", "/api/tarefas/" + tarefaId + "/subtarefas",
                "{\"nome\":\"Subtarefa " + unico("s") + "\",\"check\":false}", token);
        assertEquals(201, criar.statusCode(), criar.body());
        int subId = idDe(criar);

        HttpResponse<String> listar = send("GET", "/api/tarefas/" + tarefaId + "/subtarefas", null, token);
        assertEquals(200, listar.statusCode(), listar.body());
        assertTrue(json(listar).isArray());

        HttpResponse<String> atualizar = send("PUT", "/api/tarefas/" + tarefaId + "/subtarefas/" + subId,
                "{\"nome\":\"Subtarefa atualizada\",\"check\":false}", token);
        assertEquals(200, atualizar.statusCode(), atualizar.body());
        assertEquals("Subtarefa atualizada", json(atualizar).path("nome").asText());

        HttpResponse<String> check = send("PATCH", "/api/tarefas/" + tarefaId + "/subtarefas/" + subId + "/check",
                "{\"check\":true}", token);
        assertEquals(200, check.statusCode(), check.body());
        assertTrue(json(check).path("check").asBoolean());

        assertEquals(204, send("DELETE", "/api/tarefas/" + tarefaId + "/subtarefas/" + subId, null, token).statusCode());
    }

    // ------------------------ colaboradores --------------------------

    @Test
    void crudColaboradorDaTarefa() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];
        int colaboradorId = criarColaborador(token);

        HttpResponse<String> associar = send("POST", "/api/tarefas/" + tarefaId + "/colaboradores",
                "{\"colaboradorId\":" + colaboradorId + ",\"dataAtribuicao\":\"2026-12-01T09:00:00\"}", token);
        assertEquals(201, associar.statusCode(), associar.body());

        HttpResponse<String> listar = send("GET", "/api/tarefas/" + tarefaId + "/colaboradores", null, token);
        assertEquals(200, listar.statusCode(), listar.body());
        assertEquals(2, json(listar).size());

        assertEquals(204, send("DELETE", "/api/tarefas/" + tarefaId + "/colaboradores/" + colaboradorId,
                null, token).statusCode());

        HttpResponse<String> listarAposRemover = send("GET", "/api/tarefas/" + tarefaId + "/colaboradores", null, token);
        assertEquals(200, listarAposRemover.statusCode(), listarAposRemover.body());
        assertEquals(1, json(listarAposRemover).size());
    }

    @Test
    void semTokenRetorna401() throws Exception {
        assertEquals(401, send("GET", "/api/tarefas", null, null).statusCode());
    }
}
