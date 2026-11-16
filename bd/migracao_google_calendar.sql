-- Executar em bancos criados antes da integração com o Google Calendar.
USE gestao_faisca;

-- Quem criou cada registro (dono do evento no Google Calendar)
ALTER TABLE tarefa ADD COLUMN criadoPor INT NULL;
ALTER TABLE projeto ADD COLUMN criadoPor INT NULL;
ALTER TABLE registro_horas ADD COLUMN criadoPor INT NULL;

-- Tokens OAuth do Google Calendar por usuário
CREATE TABLE IF NOT EXISTS google_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkUsuario INT NOT NULL UNIQUE,
    access_token VARCHAR(2048),
    refresh_token VARCHAR(2048),
    expira_em BIGINT,
    CONSTRAINT fk_google_token_usuario FOREIGN KEY (fkUsuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Vinculo entre entidades do Faisca e eventos do Google Calendar
CREATE TABLE IF NOT EXISTS google_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    fkOrigem INT NOT NULL,
    fkUsuario INT NOT NULL,
    google_event_id VARCHAR(255) NOT NULL,
    UNIQUE KEY uq_google_event (tipo, fkOrigem, fkUsuario),
    CONSTRAINT fk_google_event_usuario FOREIGN KEY (fkUsuario) REFERENCES usuario(id) ON DELETE CASCADE
);
