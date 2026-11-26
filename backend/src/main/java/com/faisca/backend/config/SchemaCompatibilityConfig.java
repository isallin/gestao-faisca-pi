package com.faisca.backend.config;

import com.faisca.backend.tarefa.service.PrioridadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Configuration
public class SchemaCompatibilityConfig {

    private static final Logger log = LoggerFactory.getLogger(SchemaCompatibilityConfig.class);

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationRunner schemaCompatibilityRunner(JdbcTemplate jdbc, DataSource dataSource) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                String databaseName;
                try (Connection connection = dataSource.getConnection()) {
                    databaseName = connection.getMetaData().getDatabaseProductName();
                }
                if (databaseName == null || !databaseName.toLowerCase().contains("mysql")) return;

                // Tabelas de integração Google Calendar
                ensureGoogleTokenTable(jdbc);
                ensureGoogleEventTable(jdbc);

                // Novas tabelas V8
                ensurePrioridadeTable(jdbc);
                ensureEnderecoProjetoTable(jdbc);
                ensureSubtarefaTable(jdbc);
                ensureTarefaFaseTable(jdbc);
                ensureFornecedorEmpresaTable(jdbc);
                ensureFornecedorCategoriaTable(jdbc);
                ensureFornecedorCategAssocTable(jdbc);
                ensureFornecedorContatoTable(jdbc);
                ensureFornecedorProjetoTable(jdbc);
                ensureTarefaChecklistTable(jdbc);

                // Colunas V8 em tabelas existentes
                ensureVarcharMinLength(jdbc, "usuario", "senha", 255, "VARCHAR(255) NOT NULL");

                ensureColumn(jdbc, "cliente", "cep", "VARCHAR(8) NULL");
                ensureColumn(jdbc, "cliente", "logradouro", "VARCHAR(200) NULL");
                ensureColumn(jdbc, "cliente", "numero", "VARCHAR(10) NULL");
                ensureColumn(jdbc, "cliente", "complemento", "VARCHAR(60) NULL");
                ensureColumn(jdbc, "cliente", "bairro", "VARCHAR(80) NULL");
                ensureColumn(jdbc, "cliente", "cidade", "VARCHAR(80) NULL");
                ensureColumn(jdbc, "cliente", "estado", "VARCHAR(2) NULL");
                ensureColumn(jdbc, "cliente", "pais", "VARCHAR(80) NULL");
                ensureVarcharMinLength(jdbc, "cliente", "pais", 80, "VARCHAR(80) NULL");

                ensureColumn(jdbc, "projeto", "numero", "VARCHAR(10) NULL");
                ensureColumn(jdbc, "projeto", "criadoPor", "INT NULL");
                ensureVarcharMinLength(jdbc, "projeto", "pais", 80, "VARCHAR(80) NULL");
                relaxProjetoFkTarefaFaseColumn(jdbc);

                ensureColumn(jdbc, "tarefa", "prioridade_id", "INT NULL");
                ensureColumn(jdbc, "tarefa", "criadoPor", "INT NULL");
                relaxLegacyTarefaPrioridadeColumn(jdbc);
                relaxLegacyPrioridadeIdPrioridadeColumn(jdbc);

                normalizeColaboradorTarefaColumns(jdbc);
                ensureColumn(jdbc, "colaborador_tarefa", "data_atribuicao", "DATETIME NULL");

                ensureColumn(jdbc, "registro_horas", "fkColaborador", "INT NULL");
                ensureColumn(jdbc, "registro_horas", "ultima_atualizacao", "DATETIME NULL");
                ensureColumn(jdbc, "registro_horas", "criadoPor", "INT NULL");

                // Seeds e migração não-destrutiva de dados legados para V8
                seedPrioridades(jdbc);
                seedFornecedorCategorias(jdbc);
                backfillClienteEndereco(jdbc);
                backfillProjetoEndereco(jdbc);
                backfillTarefaPrioridade(jdbc);
                backfillTarefaFase(jdbc);
                backfillSubtarefas(jdbc);
                backfillRegistroHorasColaborador(jdbc);
                migrateLegacyFornecedores(jdbc);
            }
        };
    }

    private static void ensureGoogleTokenTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "google_token")) return;
        jdbc.execute("""
                CREATE TABLE `google_token` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `fkUsuario` INT NOT NULL UNIQUE,
                    `access_token` VARCHAR(2048),
                    `refresh_token` VARCHAR(2048),
                    `expira_em` BIGINT,
                    CONSTRAINT `fk_google_token_usuario`
                        FOREIGN KEY (`fkUsuario`) REFERENCES `usuario`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureGoogleEventTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "google_event")) return;
        jdbc.execute("""
                CREATE TABLE `google_event` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `tipo` VARCHAR(20) NOT NULL,
                    `fkOrigem` INT NOT NULL,
                    `fkUsuario` INT NOT NULL,
                    `google_event_id` VARCHAR(255) NOT NULL,
                    UNIQUE KEY `uq_google_event` (`tipo`, `fkOrigem`, `fkUsuario`),
                    CONSTRAINT `fk_google_event_usuario`
                        FOREIGN KEY (`fkUsuario`) REFERENCES `usuario`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensurePrioridadeTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "prioridade")) return;
        jdbc.execute("""
                CREATE TABLE `prioridade` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `nome` VARCHAR(45) NOT NULL
                )
                """);
    }

    private static void ensureEnderecoProjetoTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "endereco_projeto")) return;
        jdbc.execute("""
                CREATE TABLE `endereco_projeto` (
                    `idendereco_projeto` INT AUTO_INCREMENT PRIMARY KEY,
                    `projeto_id` INT NOT NULL UNIQUE,
                    `cep` VARCHAR(8),
                    `logradouro` VARCHAR(200),
                    `numero` VARCHAR(10),
                    `complemento` VARCHAR(60),
                    `bairro` VARCHAR(80),
                    `cidade` VARCHAR(80),
                    `estado` VARCHAR(2),
                    `pais` VARCHAR(80),
                    CONSTRAINT `fk_endereco_projeto_projeto`
                        FOREIGN KEY (`projeto_id`) REFERENCES `projeto`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureSubtarefaTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "subtarefa")) {
            ensureColumn(jdbc, "subtarefa", "id", "INT AUTO_INCREMENT PRIMARY KEY FIRST");
            try {
                if (columnExists(jdbc, "subtarefa", "nome")) {
                    jdbc.execute("UPDATE `subtarefa` SET `nome` = '' WHERE `nome` IS NULL");
                    ensureVarcharMinLength(jdbc, "subtarefa", "nome", 200, "VARCHAR(200) NOT NULL");
                }
                if (columnExists(jdbc, "subtarefa", "check")) {
                    jdbc.execute("UPDATE `subtarefa` SET `check` = 0 WHERE `check` IS NULL");
                    jdbc.execute("ALTER TABLE `subtarefa` MODIFY COLUMN `check` TINYINT(1) NOT NULL DEFAULT 0");
                }
            } catch (Exception e) {
                log.debug("Ajuste de colunas de subtarefa ignorado: {}", e.getMessage());
            }
            return;
        }
        jdbc.execute("""
                CREATE TABLE `subtarefa` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `tarefa_id` INT NOT NULL,
                    `nome` VARCHAR(200) NOT NULL,
                    `check` TINYINT(1) NOT NULL DEFAULT 0,
                    CONSTRAINT `fk_subtarefa_tarefa`
                        FOREIGN KEY (`tarefa_id`) REFERENCES `tarefa`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureTarefaFaseTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "tarefa_fase")) {
            normalizeTarefaFasePrimaryKey(jdbc);
            return;
        }
        jdbc.execute("""
                CREATE TABLE `tarefa_fase` (
                    `idProjeto` INT NOT NULL,
                    `idFase` INT NOT NULL,
                    `pkTarefa` INT NOT NULL,
                    PRIMARY KEY (`idProjeto`, `idFase`, `pkTarefa`),
                    CONSTRAINT `fk_tarefa_fase_projeto` FOREIGN KEY (`idProjeto`) REFERENCES `projeto`(`id`) ON DELETE CASCADE,
                    CONSTRAINT `fk_tarefa_fase_fase` FOREIGN KEY (`idFase`) REFERENCES `fase`(`idfase`),
                    CONSTRAINT `fk_tarefa_fase_tarefa` FOREIGN KEY (`pkTarefa`) REFERENCES `tarefa`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureTarefaChecklistTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "tarefa_checklist")) return;
        jdbc.execute("""
                CREATE TABLE `tarefa_checklist` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `fkTarefa` INT NOT NULL,
                    `texto` VARCHAR(200) NOT NULL,
                    `concluido` BOOLEAN NOT NULL DEFAULT FALSE,
                    CONSTRAINT `fk_checklist_tarefa`
                        FOREIGN KEY (`fkTarefa`) REFERENCES `tarefa`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureFornecedorEmpresaTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "fornecedor_empresa")) return;
        jdbc.execute("""
                CREATE TABLE `fornecedor_empresa` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `nome` VARCHAR(150) NOT NULL,
                    `cidade` VARCHAR(80),
                    `origem` VARCHAR(80),
                    `estado` CHAR(2),
                    `ja_trabalhei` TINYINT(1) DEFAULT 0,
                    `observacao` VARCHAR(300)
                )
                """);
    }

    private static void ensureFornecedorCategoriaTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "fornecedor_categoria")) return;
        jdbc.execute("""
                CREATE TABLE `fornecedor_categoria` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `nome` VARCHAR(80) NOT NULL
                )
                """);
    }

    private static void ensureFornecedorCategAssocTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "fornecedor_categ_assoc")) return;
        jdbc.execute("""
                CREATE TABLE `fornecedor_categ_assoc` (
                    `pkCategoria` INT NOT NULL,
                    `pkFornecedor` INT NOT NULL,
                    PRIMARY KEY (`pkCategoria`, `pkFornecedor`),
                    CONSTRAINT `fk_fornecedor_categ_assoc_categoria`
                        FOREIGN KEY (`pkCategoria`) REFERENCES `fornecedor_categoria`(`id`) ON DELETE CASCADE,
                    CONSTRAINT `fk_fornecedor_categ_assoc_fornecedor`
                        FOREIGN KEY (`pkFornecedor`) REFERENCES `fornecedor_empresa`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureFornecedorContatoTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "fornecedor_contato")) return;
        jdbc.execute("""
                CREATE TABLE `fornecedor_contato` (
                    `id` INT AUTO_INCREMENT PRIMARY KEY,
                    `fkFornecedor` INT NOT NULL,
                    `nome` VARCHAR(150),
                    `email` VARCHAR(120),
                    `telefone` VARCHAR(15),
                    CONSTRAINT `fk_fornecedor_contato_empresa`
                        FOREIGN KEY (`fkFornecedor`) REFERENCES `fornecedor_empresa`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void ensureFornecedorProjetoTable(JdbcTemplate jdbc) {
        if (tableExists(jdbc, "fornecedor_projeto")) return;
        jdbc.execute("""
                CREATE TABLE `fornecedor_projeto` (
                    `fornecedor_id` INT NOT NULL,
                    `projeto_id` INT NOT NULL,
                    `servico` VARCHAR(200),
                    `valor` DECIMAL(12,2),
                    `data_inicio` DATE,
                    `data_fim` DATE,
                    PRIMARY KEY (`fornecedor_id`, `projeto_id`),
                    CONSTRAINT `fk_fornecedor_projeto_fornecedor`
                        FOREIGN KEY (`fornecedor_id`) REFERENCES `fornecedor_empresa`(`id`) ON DELETE CASCADE,
                    CONSTRAINT `fk_fornecedor_projeto_projeto`
                        FOREIGN KEY (`projeto_id`) REFERENCES `projeto`(`id`) ON DELETE CASCADE
                )
                """);
    }

    private static void relaxLegacyTarefaPrioridadeColumn(JdbcTemplate jdbc) {
        if (!columnExists(jdbc, "tarefa", "prioridade")) return;
        try {
            jdbc.execute("ALTER TABLE `tarefa` MODIFY COLUMN `prioridade` VARCHAR(45) NULL DEFAULT NULL");
        } catch (Exception e) {
            log.debug("Não foi necessário ajustar coluna legada tarefa.prioridade: {}", e.getMessage());
        }
    }

    private static void relaxLegacyPrioridadeIdPrioridadeColumn(JdbcTemplate jdbc) {
        if (!columnExists(jdbc, "tarefa", "prioridade_idprioridade")) return;
        try {
            jdbc.execute("ALTER TABLE `tarefa` MODIFY COLUMN `prioridade_idprioridade` INT NULL DEFAULT NULL");
        } catch (Exception e) {
            log.debug("Não foi necessário ajustar coluna prioridade_idprioridade: {}", e.getMessage());
        }
    }

    private static void relaxProjetoFkTarefaFaseColumn(JdbcTemplate jdbc) {
        if (!columnExists(jdbc, "projeto", "fkTarefaFase")) return;
        try {
            String nullable = jdbc.queryForObject(
                    "SELECT IS_NULLABLE FROM information_schema.columns " +
                            "WHERE table_schema = DATABASE() AND table_name = 'projeto' AND column_name = 'fkTarefaFase'",
                    String.class
            );
            if ("NO".equalsIgnoreCase(nullable)) {
                jdbc.execute("ALTER TABLE `projeto` MODIFY COLUMN `fkTarefaFase` INT NULL");
            }
        } catch (Exception e) {
            log.debug("Ajuste de nulabilidade de projeto.fkTarefaFase ignorado: {}", e.getMessage());
        }
    }

    private static void normalizeTarefaFasePrimaryKey(JdbcTemplate jdbc) {
        if (!columnExists(jdbc, "tarefa_fase", "pkTarefa")) return;
        try {
            List<String> pkCols = jdbc.queryForList(
                    "SELECT column_name FROM information_schema.key_column_usage " +
                            "WHERE table_schema = DATABASE() AND table_name = 'tarefa_fase' AND constraint_name = 'PRIMARY'",
                    String.class
            );
            boolean hasPkTarefa = pkCols.stream().anyMatch(c -> "pkTarefa".equalsIgnoreCase(c));
            if (!hasPkTarefa) {
                jdbc.execute("ALTER TABLE `tarefa_fase` DROP PRIMARY KEY, ADD PRIMARY KEY (`idProjeto`, `idFase`, `pkTarefa`)");
            }
        } catch (Exception e) {
            log.debug("Ajuste de PK de tarefa_fase ignorado: {}", e.getMessage());
        }
    }

    private static void normalizeColaboradorTarefaColumns(JdbcTemplate jdbc) {
        if (!tableExists(jdbc, "colaborador_tarefa")) return;
        if (columnExists(jdbc, "colaborador_tarefa", "pfTarefa")
                && !columnExists(jdbc, "colaborador_tarefa", "pkTarefa")) {
            try {
                jdbc.execute("ALTER TABLE `colaborador_tarefa` RENAME COLUMN `pfTarefa` TO `pkTarefa`");
            } catch (Exception e) {
                log.warn("Falha ao normalizar coluna pfTarefa -> pkTarefa: {}", e.getMessage());
            }
        }
    }

    private static void seedPrioridades(JdbcTemplate jdbc) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM `prioridade`", Integer.class);
        if (count != null && count > 0) return;
        for (String nome : List.of("Baixa", "Moderada", "Média", "Alta", "Crítica")) {
            jdbc.update("INSERT INTO `prioridade` (`nome`) VALUES (?)", nome);
        }
    }

    private static void seedFornecedorCategorias(JdbcTemplate jdbc) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM `fornecedor_categoria`", Integer.class);
        if (count != null && count > 0) return;
        for (String nome : List.of("Marcenaria", "Iluminação", "Marmoraria", "Paisagismo", "Construção", "Instalações", "Materiais", "Outros")) {
            jdbc.update("INSERT INTO `fornecedor_categoria` (`nome`) VALUES (?)", nome);
        }
    }

    private static void backfillClienteEndereco(JdbcTemplate jdbc) {
        if (!tableExists(jdbc, "endereco_cliente")) return;
        try {
            jdbc.execute("""
                    UPDATE `cliente` c
                    JOIN (
                        SELECT cliente_id, MIN(idendereco_projeto) AS min_id
                        FROM `endereco_cliente`
                        GROUP BY cliente_id
                    ) first_e ON first_e.cliente_id = c.id
                    JOIN `endereco_cliente` e ON e.idendereco_projeto = first_e.min_id
                    SET c.cep = COALESCE(c.cep, e.cep),
                        c.logradouro = COALESCE(c.logradouro, e.logradouro),
                        c.numero = COALESCE(c.numero, e.numero),
                        c.complemento = COALESCE(c.complemento, e.complemento),
                        c.bairro = COALESCE(c.bairro, e.bairro),
                        c.cidade = COALESCE(c.cidade, e.cidade),
                        c.estado = COALESCE(c.estado, e.estado),
                        c.pais = COALESCE(c.pais, e.pais)
                    WHERE c.logradouro IS NULL
                    """);
        } catch (Exception e) {
            log.debug("Backfill cliente endereco ignorado: {}", e.getMessage());
        }
    }

    private static void backfillProjetoEndereco(JdbcTemplate jdbc) {
        try {
            jdbc.execute("""
                    INSERT INTO `endereco_projeto` (`projeto_id`, `cep`, `logradouro`, `numero`, `complemento`, `bairro`, `cidade`, `estado`, `pais`)
                    SELECT p.id, p.cep, p.logradouro, COALESCE(p.numero, LEFT(p.unidade_numero, 10)), p.complemento, p.bairro, p.cidade, p.estado, p.pais
                    FROM `projeto` p
                    LEFT JOIN `endereco_projeto` ep ON ep.projeto_id = p.id
                    WHERE ep.projeto_id IS NULL
                      AND (p.cep IS NOT NULL OR p.logradouro IS NOT NULL OR p.cidade IS NOT NULL)
                    """);
        } catch (Exception e) {
            log.debug("Backfill endereco_projeto ignorado: {}", e.getMessage());
        }
    }

    private static void backfillTarefaPrioridade(JdbcTemplate jdbc) {
        if (!columnExists(jdbc, "tarefa", "prioridade") || !columnExists(jdbc, "tarefa", "prioridade_id")) return;
        try {
            List<String> legadas = jdbc.queryForList(
                    "SELECT DISTINCT TRIM(prioridade) FROM `tarefa` WHERE prioridade IS NOT NULL AND TRIM(prioridade) <> ''",
                    String.class
            );
            for (String raw : legadas) {
                String normalizado = PrioridadeService.normalizarNome(raw);
                Integer exists = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM `prioridade` WHERE LOWER(nome) = LOWER(?)",
                        Integer.class,
                        normalizado
                );
                if (exists == null || exists == 0) {
                    jdbc.update("INSERT INTO `prioridade` (`nome`) VALUES (?)", normalizado);
                }
            }
            jdbc.execute("""
                    UPDATE `tarefa` t
                    JOIN `prioridade` p ON LOWER(p.nome) = LOWER(
                        CASE
                            WHEN LOWER(TRIM(t.prioridade)) = 'media' THEN 'Média'
                            WHEN LOWER(TRIM(t.prioridade)) = 'critica' THEN 'Crítica'
                            ELSE TRIM(t.prioridade)
                        END
                    )
                    SET t.prioridade_id = p.id
                    WHERE t.prioridade_id IS NULL AND t.prioridade IS NOT NULL AND TRIM(t.prioridade) <> ''
                    """);
        } catch (Exception e) {
            log.debug("Backfill tarefa.prioridade_id ignorado: {}", e.getMessage());
        }
    }

    private static void backfillTarefaFase(JdbcTemplate jdbc) {
        try {
            jdbc.execute("""
                    INSERT IGNORE INTO `tarefa_fase` (`idProjeto`, `idFase`, `pkTarefa`)
                    SELECT t.fkProjeto, t.fkTarefaFase, t.id
                    FROM `tarefa` t
                    WHERE t.fkProjeto IS NOT NULL AND t.fkTarefaFase IS NOT NULL
                    """);
        } catch (Exception e) {
            log.debug("Backfill tarefa_fase ignorado: {}", e.getMessage());
        }
    }

    private static void backfillSubtarefas(JdbcTemplate jdbc) {
        if (!tableExists(jdbc, "tarefa_checklist")) return;
        try {
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM `subtarefa`", Integer.class);
            if (count != null && count > 0) return;
            jdbc.execute("""
                    INSERT INTO `subtarefa` (`tarefa_id`, `nome`, `check`)
                    SELECT tc.fkTarefa, tc.texto, tc.concluido
                    FROM `tarefa_checklist` tc
                    """);
        } catch (Exception e) {
            log.debug("Backfill subtarefa ignorado: {}", e.getMessage());
        }
    }

    private static void backfillRegistroHorasColaborador(JdbcTemplate jdbc) {
        try {
            jdbc.execute("""
                    UPDATE `registro_horas` rh
                    SET rh.fkColaborador = COALESCE(
                        rh.fkColaborador,
                        rh.criadoPor,
                        (SELECT ct.pkColaborador FROM `colaborador_tarefa` ct WHERE ct.pkTarefa = rh.fkTarefa LIMIT 1)
                    ),
                    rh.ultima_atualizacao = COALESCE(rh.ultima_atualizacao, NOW())
                    WHERE rh.fkColaborador IS NULL OR rh.ultima_atualizacao IS NULL
                    """);
            jdbc.execute("""
                    INSERT IGNORE INTO `colaborador_tarefa` (`pkTarefa`, `pkColaborador`, `data_atribuicao`)
                    SELECT rh.fkTarefa, rh.fkColaborador, COALESCE(rh.hora_inicio, NOW())
                    FROM `registro_horas` rh
                    WHERE rh.fkColaborador IS NOT NULL
                    """);
        } catch (Exception e) {
            log.debug("Backfill registro_horas.fkColaborador ignorado: {}", e.getMessage());
        }
    }

    private static void migrateLegacyFornecedores(JdbcTemplate jdbc) {
        if (!tableExists(jdbc, "fornecedor")) return;
        try {
            Integer existing = jdbc.queryForObject("SELECT COUNT(*) FROM `fornecedor_empresa`", Integer.class);
            if (existing != null && existing > 0) return;
            List<Map<String, Object>> legacyRows = jdbc.queryForList("SELECT * FROM `fornecedor`");
            for (Map<String, Object> row : legacyRows) {
                String nome = asString(row.get("nome"));
                if (nome == null || nome.isBlank()) continue;
                String cidade = asString(row.get("cidade"));
                String observacao = asString(row.get("observacao"));
                String projectsText = asString(row.get("projetos"));
                boolean jaTrabalhei = projectsText != null && !projectsText.isBlank();

                jdbc.update(
                        "INSERT INTO `fornecedor_empresa` (`nome`, `cidade`, `ja_trabalhei`, `observacao`) VALUES (?, ?, ?, ?)",
                        nome, cidade, jaTrabalhei ? 1 : 0, observacao
                );
                Integer empresaId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
                if (empresaId == null) continue;

                String categoria = asString(row.get("categoria"));
                if (categoria != null && !categoria.isBlank()) {
                    List<Integer> catIds = jdbc.queryForList(
                            "SELECT id FROM `fornecedor_categoria` WHERE LOWER(nome) = LOWER(?)",
                            Integer.class, categoria.trim());
                    Integer catId;
                    if (catIds.isEmpty()) {
                        jdbc.update("INSERT INTO `fornecedor_categoria` (`nome`) VALUES (?)", categoria.trim());
                        catId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
                    } else {
                        catId = catIds.get(0);
                    }
                    if (catId != null) {
                        jdbc.update("INSERT IGNORE INTO `fornecedor_categ_assoc` (`pkCategoria`, `pkFornecedor`) VALUES (?, ?)", catId, empresaId);
                    }
                }

                String tel1 = trunc(asString(row.get("telefone")), 15);
                String email1 = asString(row.get("email"));
                if ((tel1 != null && !tel1.isBlank()) || (email1 != null && !email1.isBlank())) {
                    jdbc.update(
                            "INSERT INTO `fornecedor_contato` (`fkFornecedor`, `nome`, `email`, `telefone`) VALUES (?, ?, ?, ?)",
                            empresaId, "Contato Principal", email1, tel1
                    );
                }

                String tel2 = trunc(asString(row.get("telefone2")), 15);
                String email2 = asString(row.get("email2"));
                if ((tel2 != null && !tel2.isBlank()) || (email2 != null && !email2.isBlank())) {
                    jdbc.update(
                            "INSERT INTO `fornecedor_contato` (`fkFornecedor`, `nome`, `email`, `telefone`) VALUES (?, ?, ?, ?)",
                            empresaId, "Contato Secundário", email2, tel2
                    );
                }

                if (projectsText != null && !projectsText.isBlank()) {
                    for (String rawCode : projectsText.split("[,;]")) {
                        String code = rawCode.trim();
                        if (code.isEmpty()) continue;
                        List<Integer> projIds = jdbc.queryForList(
                                "SELECT id FROM `projeto` WHERE LOWER(codigo) = LOWER(?)",
                                Integer.class, code);
                        if (!projIds.isEmpty()) {
                            jdbc.update(
                                    "INSERT IGNORE INTO `fornecedor_projeto` (`fornecedor_id`, `projeto_id`) VALUES (?, ?)",
                                    empresaId, projIds.get(0)
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Migração automática da tabela legada fornecedor ignorada: {}", e.getMessage());
        }
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static String trunc(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static boolean tableExists(JdbcTemplate jdbc, String tableName) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private static boolean columnExists(JdbcTemplate jdbc, String table, String column) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                table,
                column
        );
        return count != null && count > 0;
    }

    private static void ensureColumn(JdbcTemplate jdbc, String table, String column, String definition) {
        if (!tableExists(jdbc, table)) return;
        if (!columnExists(jdbc, table, column)) {
            jdbc.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
        }
    }

    private static void ensureVarcharMinLength(JdbcTemplate jdbc, String table, String column, int minLength, String definition) {
        if (!columnExists(jdbc, table, column)) return;
        try {
            Long current = jdbc.queryForObject(
                    "SELECT CHARACTER_MAXIMUM_LENGTH FROM information_schema.columns " +
                            "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                    Long.class,
                    table,
                    column
            );
            if (current != null && current < minLength) {
                jdbc.execute("ALTER TABLE `" + table + "` MODIFY COLUMN `" + column + "` " + definition);
            }
        } catch (Exception e) {
            log.debug("Ajuste de tamanho de {}.{} ignorado: {}", table, column, e.getMessage());
        }
    }
}
