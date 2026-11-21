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

                ensureColumn(jdbc, "projeto", "criadoPor", "INT NULL");
                ensureColumn(jdbc, "tarefa", "criadoPor", "INT NULL");
                ensureColumn(jdbc, "registro_horas", "criadoPor", "INT NULL");
            }
        };
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
