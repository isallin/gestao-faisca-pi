package com.faisca.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class SchemaCompatibilityConfig {

    @Bean
    public ApplicationRunner schemaCompatibilityRunner(JdbcTemplate jdbc, DataSource dataSource) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                String databaseName;
                try (Connection connection = dataSource.getConnection()) {
                    databaseName = connection.getMetaData().getDatabaseProductName();
                }
                if (databaseName == null || !databaseName.toLowerCase().contains("mysql")) return;

                ensureGoogleTokenTable(jdbc);
                ensureGoogleEventTable(jdbc);
                ensureColumn(jdbc, "projeto", "criadoPor", "INT NULL");
                ensureColumn(jdbc, "tarefa", "criadoPor", "INT NULL");
                ensureColumn(jdbc, "registro_horas", "criadoPor", "INT NULL");
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

    private static boolean tableExists(JdbcTemplate jdbc, String tableName) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private static void ensureColumn(JdbcTemplate jdbc, String table, String column, String definition) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                table,
                column
        );

        if (count != null && count == 0) {
            jdbc.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
        }
    }
}
