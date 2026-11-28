package com.faisca.backend.projeto;

import com.faisca.backend.tarefa.EndpointTestBase;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjetoEndpointTests extends EndpointTestBase {

    private Map<String, Object> payloadValido(int clienteId, int faseId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("clienteId", clienteId);
        map.put("codigo", "PRJ-" + unico("c"));
        map.put("nome", "Casa Paineiras");
        map.put("tipoImovel", "Residencial");
        map.put("areaM2", 180.5);
        map.put("cep", "01310-100");
        map.put("logradouro", "Avenida Paulista");
        map.put("numero", "248");
        map.put("complemento", "Apto 12");
        map.put("bairro", "Bela Vista");
        map.put("cidade", "São Paulo");
        map.put("estado", "SP");
        map.put("pais", "Brasil");
        map.put("fkTarefaFase", faseId);
        map.put("dataInicio", "2026-11-01T00:00:00");
        map.put("dataFim", "2026-12-30T00:00:00");
        return map;
    }

    @Test
    void permiteCadastrarSemCondominioMatriculaEObservacao() throws Exception {
        String token = login();
        int clienteId = criarCliente(token);
        int faseId = criarFase(token);

        Map<String, Object> payload = payloadValido(clienteId, faseId);
        HttpResponse<String> resp = send("POST", "/api/projetos", mapper.writeValueAsString(payload), token);

        assertEquals(201, resp.statusCode(), resp.body());
        var body = json(resp);
        assertTrue(body.path("id").asInt() > 0);
        assertNull(body.get("condominio").isNull() ? null : body.get("condominio").asText());
        assertNull(body.get("matricula").isNull() ? null : body.get("matricula").asText());
        assertNull(body.get("observacao").isNull() ? null : body.get("observacao").asText());
    }

    @Test
    void rejeitaCadastroQuandoQualquerCampoObrigatorioEstaAusente() throws Exception {
        String token = login();
        int clienteId = criarCliente(token);
        int faseId = criarFase(token);

        List<String> obrigatorios = List.of(
                "clienteId", "codigo", "nome", "tipoImovel", "areaM2",
                "cep", "logradouro", "numero", "complemento", "bairro",
                "cidade", "estado", "pais", "fkTarefaFase", "dataInicio", "dataFim"
        );

        for (String campo : obrigatorios) {
            Map<String, Object> payload = payloadValido(clienteId, faseId);
            payload.remove(campo);
            HttpResponse<String> resp = send("POST", "/api/projetos", mapper.writeValueAsString(payload), token);
            assertEquals(400, resp.statusCode(), "Esperava 400 ao omitir campo obrigatório: " + campo + " -> " + resp.body());
        }
    }
}
