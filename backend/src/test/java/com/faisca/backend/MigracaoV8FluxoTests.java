package com.faisca.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:v8flow;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MigracaoV8FluxoTests {

    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> send(String method, String path, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (json != null) builder.header("Content-Type", "application/json");
        return http.send(builder.method(method, json == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void fluxoCompletoEntidadesRelacionamentosV8EColaboradores() throws Exception {
        // 0. Garante que o mapeamento JPA da tabela tarefa não referencia a coluna legada 'prioridade'
        List<String> colunasTarefa = jdbc.queryForList(
                "SELECT LOWER(COLUMN_NAME) FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) = 'tarefa'",
                String.class
        );
        assertTrue(colunasTarefa.contains("prioridade_id"), "Coluna prioridade_id deve existir em tarefa: " + colunasTarefa);
        assertFalse(colunasTarefa.contains("prioridade"), "Coluna legada prioridade não deve ser mapeada em tarefa: " + colunasTarefa);

        // 1. Login com usuário bootstrap + criação de colaboradores
        HttpResponse<String> loginAdmin = send("POST", "/api/auth/login",
                "{\"email\":\"teste@faisca.com\",\"senha\":\"123456\"}", null);
        assertEquals(200, loginAdmin.statusCode(), loginAdmin.body());
        String token = mapper.readTree(loginAdmin.body()).path("token").asText();
        int adminId = mapper.readTree(loginAdmin.body()).path("usuario").path("id").asInt();

        HttpResponse<String> colabCreate = send("POST", "/api/usuarios",
                "{\"nome\":\"Ana Paula\",\"sobrenome\":\"Mendes\",\"email\":\"ana.mendes@faisca.com\",\"cargo\":\"Arquiteta\",\"senha\":\"senha12345\"}", null);
        assertEquals(201, colabCreate.statusCode(), colabCreate.body());
        int colabId = mapper.readTree(colabCreate.body()).path("id").asInt();

        // Edição de colaborador com alteração opcional de senha (correção do bug de Colaboradores)
        HttpResponse<String> colabPut = send("PUT", "/api/usuarios/" + colabId,
                "{\"nome\":\"Ana Paula\",\"sobrenome\":\"Mendes\",\"email\":\"ana.mendes@faisca.com\",\"cargo\":\"Arquiteta Sênior\",\"senha\":\"novasenha123\"}", token);
        assertEquals(200, colabPut.statusCode(), colabPut.body());
        assertEquals("Arquiteta Sênior", mapper.readTree(colabPut.body()).path("cargo").asText());

        // Confirma que a nova senha funciona no login
        HttpResponse<String> loginColab = send("POST", "/api/auth/login",
                "{\"email\":\"ana.mendes@faisca.com\",\"senha\":\"novasenha123\"}", null);
        assertEquals(200, loginColab.statusCode(), loginColab.body());

        // 2. Fase CRUD + Categoria de Tarefa CRUD + Prioridade CRUD
        HttpResponse<String> faseResp = send("POST", "/api/fases", "{\"nome\":\"Executivo\",\"ordem\":1}", token);
        assertEquals(201, faseResp.statusCode(), faseResp.body());
        int faseId = mapper.readTree(faseResp.body()).path("id").asInt();

        HttpResponse<String> catResp = send("POST", "/api/tarefas/categorias", "{\"nome\":\"Projeto\"}", token);
        assertEquals(201, catResp.statusCode(), catResp.body());
        int categoriaId = mapper.readTree(catResp.body()).path("id").asInt();

        HttpResponse<String> prioList = send("GET", "/api/prioridades", null, token);
        assertEquals(200, prioList.statusCode(), prioList.body());
        JsonNode prioArr = mapper.readTree(prioList.body());
        assertTrue(prioArr.size() >= 5, "Seed de prioridades V8 deve estar populado");
        int prioridadeId = prioArr.get(0).path("id").asInt();

        // 3. Cliente PJ + Endereços + Representantes Legais
        HttpResponse<String> cliResp = send("POST", "/api/clientes",
                "{\"tipo\":\"PJ\",\"nomeFantasia\":\"Studio Aurora\",\"cnpj\":\"11222333000181\","
                        + "\"telefone\":\"(11) 98765-4321\",\"email\":\"contato@aurora.com\","
                        + "\"cep\":\"01310-100\",\"logradouro\":\"Av Paulista\",\"numero\":\"1000\","
                        + "\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"pais\":\"Brasil\"}", token);
        assertEquals(201, cliResp.statusCode(), cliResp.body());
        int clienteId = mapper.readTree(cliResp.body()).path("id").asInt();

        HttpResponse<String> repResp = send("POST", "/api/clientes/" + clienteId + "/representantes",
                "{\"nomeCompleto\":\"Carla Dias\",\"cargo\":\"Diretora\",\"email\":\"carla@aurora.com\",\"telefone\":\"(11) 99999-8888\"}", token);
        assertEquals(201, repResp.statusCode(), repResp.body());
        assertEquals(1, mapper.readTree(send("GET", "/api/clientes/" + clienteId + "/representantes", null, token).body()).size());

        // 4. Projeto + EnderecoProjeto (1:1)
        HttpResponse<String> projResp = send("POST", "/api/projetos",
                "{\"clienteId\":" + clienteId + ",\"codigo\":\"RES-100\",\"nome\":\"Residência Aurora\","
                        + "\"fkTarefaFase\":" + faseId + ",\"cep\":\"01310-100\",\"logradouro\":\"Rua Oscar Freire\","
                        + "\"numero\":\"250\",\"bairro\":\"Jardins\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"pais\":\"Brasil\"}", token);
        assertEquals(201, projResp.statusCode(), projResp.body());
        JsonNode projJson = mapper.readTree(projResp.body());
        int projetoId = projJson.path("id").asInt();
        assertEquals("250", projJson.path("numero").asText());
        assertEquals("Rua Oscar Freire", projJson.path("endereco").path("logradouro").asText());

        HttpResponse<String> epGet = send("GET", "/api/projetos/" + projetoId + "/endereco", null, token);
        assertEquals(200, epGet.statusCode(), epGet.body());
        assertEquals("250", mapper.readTree(epGet.body()).path("numero").asText());

        // 5. Fornecedor V8 (empresa + categorias N:N + contatos 1:N + projetos N:N)
        HttpResponse<String> fornResp = send("POST", "/api/fornecedores",
                "{\"nome\":\"Marcenaria Fina\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"origem\":\"Indicação\","
                        + "\"categorias\":[\"Marcenaria\",\"Acabamentos\"],"
                        + "\"contatos\":[{\"nome\":\"João\",\"email\":\"joao@marcenaria.com\",\"telefone\":\"11988887777\"}],"
                        + "\"projetosVinculados\":[{\"projetoId\":" + projetoId + ",\"servico\":\"Cozinha\",\"valor\":45000.00,\"dataInicio\":\"2026-11-01\",\"dataFim\":\"2026-12-15\"}]}", token);
        assertEquals(201, fornResp.statusCode(), fornResp.body());
        JsonNode fornJson = mapper.readTree(fornResp.body());
        int fornecedorId = fornJson.path("id").asInt();
        assertEquals(2, fornJson.path("categorias").size());
        assertEquals(1, fornJson.path("contatos").size());
        assertEquals(1, fornJson.path("projetosVinculados").size());
        assertEquals("RES-100", fornJson.path("projects").asText());
        assertTrue(fornJson.path("jaTrabalhei").asBoolean());

        // Consulta inversa pelo projeto
        assertEquals(1, mapper.readTree(send("GET", "/api/projetos/" + projetoId + "/fornecedores", null, token).body()).size());

        // 6. Tarefa + Prioridade FK + Subtarefa + ColaboradorTarefa (N:N) + TarefaFase + RegistroHoras (fkColaborador)
        HttpResponse<String> tarResp = send("POST", "/api/tarefas",
                "{\"projetoId\":" + projetoId + ",\"categoriaId\":" + categoriaId + ",\"fkTarefaFase\":" + faseId + ","
                        + "\"nome\":\"Detalhar iluminação\",\"prioridadeId\":" + prioridadeId + ","
                        + "\"dataInicioPrev\":\"2026-11-20T09:00:00\",\"dataFimPrev\":\"2026-11-25T18:00:00\","
                        + "\"status\":\"Em andamento\",\"colaboradoresIds\":[" + colabId + "," + adminId + "],"
                        + "\"subtarefas\":[{\"nome\":\"Definir luminárias\",\"check\":false}]}", token);
        assertEquals(201, tarResp.statusCode(), tarResp.body());
        JsonNode tarJson = mapper.readTree(tarResp.body());
        int tarefaId = tarJson.path("id").asInt();
        assertEquals(prioridadeId, tarJson.path("prioridadeId").asInt());
        assertEquals(2, tarJson.path("colaboradoresIds").size());
        assertEquals(1, tarJson.path("subtarefas").size());
        assertEquals(1, tarJson.path("checklist").size());

        // Verifica subtarefa PATCH check
        int subId = tarJson.path("subtarefas").get(0).path("id").asInt();
        HttpResponse<String> subCheck = send("PATCH", "/api/tarefas/" + tarefaId + "/subtarefas/" + subId + "/check", "{\"check\":true}", token);
        assertEquals(200, subCheck.statusCode(), subCheck.body());
        assertTrue(mapper.readTree(subCheck.body()).path("check").asBoolean());

        // Verifica tarefa_fase
        HttpResponse<String> tfList = send("GET", "/api/projetos/" + projetoId + "/fases", null, token);
        assertEquals(200, tfList.statusCode(), tfList.body());
        assertEquals(1, mapper.readTree(tfList.body()).size());

        // Verifica listagens globais (tarefas, usuários e dashboard) sem coluna legada tarefa.prioridade
        HttpResponse<String> tarefasListResp = send("GET", "/api/tarefas", null, token);
        assertEquals(200, tarefasListResp.statusCode(), tarefasListResp.body());
        assertEquals(prioridadeId, mapper.readTree(tarefasListResp.body()).get(0).path("prioridadeId").asInt());
        assertFalse(mapper.readTree(tarefasListResp.body()).get(0).path("prioridade").asText().isBlank());

        HttpResponse<String> usuariosListResp = send("GET", "/api/usuarios", null, token);
        assertEquals(200, usuariosListResp.statusCode(), usuariosListResp.body());

        // Cria prioridade customizada, associa a uma tarefa para hoje, renomeia a prioridade e verifica reflexo em /tarefas e /dashboard
        HttpResponse<String> customPrioCreate = send("POST", "/api/prioridades", "{\"nome\":\"Urgente\"}", token);
        assertEquals(201, customPrioCreate.statusCode(), customPrioCreate.body());
        int customPrioId = mapper.readTree(customPrioCreate.body()).path("id").asInt();

        String hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();
        HttpResponse<String> tarHojeResp = send("POST", "/api/tarefas",
                "{\"projetoId\":" + projetoId + ",\"categoriaId\":" + categoriaId + ",\"fkTarefaFase\":" + faseId + ","
                        + "\"nome\":\"Entrega urgente\",\"prioridade\":\"Urgente\",\"prioridadeId\":" + customPrioId + ","
                        + "\"dataInicioPrev\":\"" + hoje + "T08:00:00\",\"dataFimPrev\":\"" + hoje + "T18:00:00\","
                        + "\"status\":\"Em andamento\",\"colaboradoresIds\":[" + colabId + "]}", token);
        assertEquals(201, tarHojeResp.statusCode(), tarHojeResp.body());
        int tarHojeId = mapper.readTree(tarHojeResp.body()).path("id").asInt();
        assertEquals("Urgente", mapper.readTree(tarHojeResp.body()).path("prioridade").asText());

        // Renomear prioridade na tabela prioridade reflete imediatamente em tarefa e dashboard
        HttpResponse<String> customPrioPut = send("PUT", "/api/prioridades/" + customPrioId, "{\"nome\":\"Emergencial\"}", token);
        assertEquals(200, customPrioPut.statusCode(), customPrioPut.body());

        HttpResponse<String> tarHojeGet = send("GET", "/api/tarefas/" + tarHojeId, null, token);
        assertEquals(200, tarHojeGet.statusCode(), tarHojeGet.body());
        assertEquals("Emergencial", mapper.readTree(tarHojeGet.body()).path("prioridade").asText());

        HttpResponse<String> dashResp = send("GET", "/api/dashboard?projeto=RES-100", null, token);
        assertEquals(200, dashResp.statusCode(), dashResp.body());
        JsonNode urgentes = mapper.readTree(dashResp.body()).path("urgentToday");
        assertEquals(1, urgentes.size(), dashResp.body());
        assertEquals("Emergencial", urgentes.get(0).path("priority").asText());

        // Prioridade vinculada não pode ser excluída (409); após excluir a tarefa, permite excluir (204)
        assertEquals(409, send("DELETE", "/api/prioridades/" + customPrioId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/tarefas/" + tarHojeId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/prioridades/" + customPrioId, null, token).statusCode());

        // Verifica que Colaboradores agora retorna "projetos" envolvidos ("RES-100")!
        HttpResponse<String> colabGet = send("GET", "/api/usuarios/" + colabId, null, token);
        assertEquals(200, colabGet.statusCode(), colabGet.body());
        JsonNode colabJson = mapper.readTree(colabGet.body());
        assertEquals("RES-100", colabJson.path("projetos").asText());
        assertEquals(1, colabJson.path("tarefasIds").size());

        HttpResponse<String> colabTarefas = send("GET", "/api/usuarios/" + colabId + "/tarefas", null, token);
        assertEquals(200, colabTarefas.statusCode(), colabTarefas.body());
        assertEquals(1, mapper.readTree(colabTarefas.body()).size());
        assertFalse(mapper.readTree(colabTarefas.body()).get(0).path("dataAtribuicao").asText().isBlank());

        // Registro de horas vinculado a colaborador + ultimaAtualizacao
        HttpResponse<String> regResp = send("POST", "/api/registros",
                "{\"tarefaId\":" + tarefaId + ",\"colaboradorId\":" + colabId + ","
                        + "\"inicio\":\"2026-11-21T09:00:00\",\"fim\":\"2026-11-21T11:30:00\",\"descricao\":\"Especificação técnica\"}", token);
        assertEquals(201, regResp.statusCode(), regResp.body());
        JsonNode regJson = mapper.readTree(regResp.body());
        assertEquals(colabId, regJson.path("colaboradorId").asInt());
        assertEquals("Ana Paula Mendes", regJson.path("colaboradorNome").asText());
        assertFalse(regJson.path("ultimaAtualizacao").asText().isBlank());

        // Integridade: não pode remover colaborador da tarefa enquanto tiver registro_horas (409)
        assertEquals(409, send("DELETE", "/api/tarefas/" + tarefaId + "/colaboradores/" + colabId, null, token).statusCode());

        // Integridade: não pode excluir usuário enquanto tiver tarefas/registros vinculados (409 em vez de 500!)
        HttpResponse<String> delColabBlocked = send("DELETE", "/api/usuarios/" + colabId, null, token);
        assertEquals(409, delColabBlocked.statusCode(), delColabBlocked.body());
        assertTrue(delColabBlocked.body().contains("vinculados"), delColabBlocked.body());

        // Não pode excluir a própria conta (409 com mensagem clara)
        HttpResponse<String> delSelf = send("DELETE", "/api/usuarios/" + adminId, null, token);
        assertEquals(409, delSelf.statusCode(), delSelf.body());
        assertTrue(delSelf.body().contains("própria conta"), delSelf.body());

        // Criar e excluir colaborador sem vínculos -> 204
        HttpResponse<String> livreResp = send("POST", "/api/usuarios",
                "{\"nome\":\"Carlos\",\"sobrenome\":\"Livre\",\"email\":\"carlos.livre@faisca.com\",\"cargo\":\"Estagiário\",\"senha\":\"senha12345\"}", null);
        assertEquals(201, livreResp.statusCode());
        int livreId = mapper.readTree(livreResp.body()).path("id").asInt();
        assertEquals(204, send("DELETE", "/api/usuarios/" + livreId, null, token).statusCode());

        // Excluir tarefa limpa registros e associações sem erro de FK
        assertEquals(204, send("DELETE", "/api/tarefas/" + tarefaId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/fornecedores/" + fornecedorId + "/projetos/" + projetoId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/fornecedores/" + fornecedorId, null, token).statusCode());
        assertEquals(204, send("DELETE", "/api/projetos/" + projetoId, null, token).statusCode());
    }
}
