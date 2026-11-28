package com.faisca.backend.fornecedor;

import com.faisca.backend.tarefa.EndpointTestBase;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FornecedorEndpointTests extends EndpointTestBase {

    private Map<String, Object> payloadValido() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Fornecedor " + unico("f"));
        map.put("category", "Marcenaria");
        map.put("phone", "(11) 98765-4321");
        map.put("email", "contato@fornecedor.com.br");
        map.put("city", "São Paulo");
        return map;
    }

    @Test
    void permiteCadastrarEAtualizarComCamposObrigatoriosEOpcionaisVazios() throws Exception {
        String token = login();

        Map<String, Object> criarPayload = payloadValido();
        HttpResponse<String> criado = send("POST", "/api/fornecedores", mapper.writeValueAsString(criarPayload), token);
        assertEquals(201, criado.statusCode(), criado.body());
        int id = idDe(criado);
        assertTrue(id > 0);

        Map<String, Object> editarPayload = payloadValido();
        editarPayload.put("city", "Campinas");
        HttpResponse<String> atualizado = send("PUT", "/api/fornecedores/" + id, mapper.writeValueAsString(editarPayload), token);
        assertEquals(200, atualizado.statusCode(), atualizado.body());
        assertEquals("Campinas", json(atualizado).path("city").asText());
    }

    @Test
    void rejeitaCadastroEEdicaoSemTelefone1Email1OuCidade() throws Exception {
        String token = login();

        // Cria um fornecedor válido para testar a validação na edição (PUT)
        HttpResponse<String> criado = send("POST", "/api/fornecedores", mapper.writeValueAsString(payloadValido()), token);
        assertEquals(201, criado.statusCode(), criado.body());
        int id = idDe(criado);

        List<String> obrigatorios = List.of("phone", "email", "city");
        for (String campo : obrigatorios) {
            Map<String, Object> payloadPost = payloadValido();
            payloadPost.remove(campo);
            HttpResponse<String> respPost = send("POST", "/api/fornecedores", mapper.writeValueAsString(payloadPost), token);
            assertEquals(400, respPost.statusCode(), "POST deveria retornar 400 sem " + campo + ": " + respPost.body());

            Map<String, Object> payloadPut = payloadValido();
            payloadPut.put(campo, "");
            HttpResponse<String> respPut = send("PUT", "/api/fornecedores/" + id, mapper.writeValueAsString(payloadPut), token);
            assertEquals(400, respPut.statusCode(), "PUT deveria retornar 400 com " + campo + " vazio: " + respPut.body());
        }
    }

    @Test
    void validaCamposObrigatoriosNoFormatoV8() throws Exception {
        String token = login();

        String validoV8 = "{\"nome\":\"Fornecedor V8 " + unico("v") + "\",\"categoria\":\"Iluminação\","
                + "\"cidade\":\"Curitiba\",\"contatos\":[{\"nome\":\"Principal\",\"telefone\":\"41998887766\",\"email\":\"v8@fornecedor.com\"}]}";
        HttpResponse<String> ok = send("POST", "/api/fornecedores", validoV8, token);
        assertEquals(201, ok.statusCode(), ok.body());

        String semEmailV8 = "{\"nome\":\"Fornecedor V8\",\"categoria\":\"Iluminação\","
                + "\"cidade\":\"Curitiba\",\"contatos\":[{\"nome\":\"Principal\",\"telefone\":\"41998887766\"}]}";
        assertEquals(400, send("POST", "/api/fornecedores", semEmailV8, token).statusCode());

        String semTelefoneV8 = "{\"nome\":\"Fornecedor V8\",\"categoria\":\"Iluminação\","
                + "\"cidade\":\"Curitiba\",\"contatos\":[{\"nome\":\"Principal\",\"email\":\"v8@fornecedor.com\"}]}";
        assertEquals(400, send("POST", "/api/fornecedores", semTelefoneV8, token).statusCode());

        String semCidadeV8 = "{\"nome\":\"Fornecedor V8\",\"categoria\":\"Iluminação\","
                + "\"contatos\":[{\"nome\":\"Principal\",\"telefone\":\"41998887766\",\"email\":\"v8@fornecedor.com\"}]}";
        assertEquals(400, send("POST", "/api/fornecedores", semCidadeV8, token).statusCode());
    }
}
