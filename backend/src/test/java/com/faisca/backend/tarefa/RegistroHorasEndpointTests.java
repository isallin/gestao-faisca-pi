package com.faisca.backend.tarefa;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/** Endpoints de /api/registros (registro de horas nas tarefas). */
class RegistroHorasEndpointTests extends EndpointTestBase {

    /** [tarefaId, colaboradorId] */
    private int[] criarTarefaComColaborador(String token) throws Exception {
        int tarefaId = criarCenario(token)[3];
        int colaboradorId = criarColaborador(token);
        return new int[]{tarefaId, colaboradorId};
    }

    @Test
    void listarRegistrosRetorna200() throws Exception {
        String token = login();
        HttpResponse<String> resp = send("GET", "/api/registros", null, token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertTrue(json(resp).isArray());
    }

    @Test
    void criarEBuscarRegistroDeHoras() throws Exception {
        String token = login();
        int[] cenario = criarTarefaComColaborador(token);

        HttpResponse<String> criar = send("POST", "/api/registros",
                "{\"tarefaId\":" + cenario[0] + ",\"colaboradorId\":" + cenario[1] + ","
                        + "\"inicio\":\"2026-12-01T09:00:00\",\"fim\":\"2026-12-01T11:30:00\","
                        + "\"descricao\":\"Especificação técnica\"}", token);
        assertEquals(201, criar.statusCode(), criar.body());
        int registroId = idDe(criar);

        HttpResponse<String> buscar = send("GET", "/api/registros/" + registroId, null, token);
        assertEquals(200, buscar.statusCode(), buscar.body());
        assertEquals(registroId, json(buscar).path("id").asInt());
    }

    @Test
    void criarRegistroSemFimRetorna400() throws Exception {
        String token = login();
        int tarefaId = criarCenario(token)[3];
        HttpResponse<String> resp = send("POST", "/api/registros",
                "{\"tarefaId\":" + tarefaId + ",\"inicio\":\"2026-12-01T09:00:00\"}", token);
        assertEquals(400, resp.statusCode(), resp.body());
    }

    @Test
    void buscarRegistroInexistenteRetorna404() throws Exception {
        String token = login();
        assertEquals(404, send("GET", "/api/registros/999999", null, token).statusCode());
    }

    @Test
    void atualizarRegistroRetorna200() throws Exception {
        String token = login();
        int[] cenario = criarTarefaComColaborador(token);
        HttpResponse<String> criar = send("POST", "/api/registros",
                "{\"tarefaId\":" + cenario[0] + ",\"colaboradorId\":" + cenario[1] + ","
                        + "\"inicio\":\"2026-12-01T09:00:00\",\"fim\":\"2026-12-01T10:00:00\","
                        + "\"descricao\":\"Descrição original\"}", token);
        int registroId = idDe(criar);

        HttpResponse<String> resp = send("PUT", "/api/registros/" + registroId,
                "{\"tarefaId\":" + cenario[0] + ",\"colaboradorId\":" + cenario[1] + ","
                        + "\"inicio\":\"2026-12-01T09:00:00\",\"fim\":\"2026-12-01T12:00:00\","
                        + "\"descricao\":\"Descrição atualizada\"}", token);
        assertEquals(200, resp.statusCode(), resp.body());
        assertEquals("Descrição atualizada", json(resp).path("descricao").asText());
    }

    @Test
    void excluirRegistroRetorna204() throws Exception {
        String token = login();
        int[] cenario = criarTarefaComColaborador(token);
        HttpResponse<String> criar = send("POST", "/api/registros",
                "{\"tarefaId\":" + cenario[0] + ",\"colaboradorId\":" + cenario[1] + ","
                        + "\"inicio\":\"2026-12-01T09:00:00\",\"fim\":\"2026-12-01T10:00:00\","
                        + "\"descricao\":\"Registro descartável\"}", token);
        int registroId = idDe(criar);
        assertEquals(204, send("DELETE", "/api/registros/" + registroId, null, token).statusCode());
    }

    @Test
    void semTokenRetorna401() throws Exception {
        assertEquals(401, send("GET", "/api/registros", null, null).statusCode());
    }
}
