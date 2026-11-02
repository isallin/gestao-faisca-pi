-- Execute uma vez em bancos criados antes da integração.
USE gestao_faisca;
CREATE TABLE IF NOT EXISTS fornecedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    telefone VARCHAR(20),
    telefone2 VARCHAR(20),
    email VARCHAR(120),
    email2 VARCHAR(120),
    cidade VARCHAR(80),
    projetos VARCHAR(255),
    observacao VARCHAR(300)
);
ALTER TABLE registro_horas ADD COLUMN descricao VARCHAR(300);
CREATE TABLE IF NOT EXISTS tarefa_checklist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkTarefa INT NOT NULL,
    texto VARCHAR(200) NOT NULL,
    concluido BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_checklist_tarefa FOREIGN KEY (fkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);
ALTER TABLE projeto
    ADD COLUMN cep VARCHAR(8), ADD COLUMN logradouro VARCHAR(200),
    ADD COLUMN complemento VARCHAR(60), ADD COLUMN bairro VARCHAR(80),
    ADD COLUMN cidade VARCHAR(80), ADD COLUMN estado VARCHAR(2), ADD COLUMN pais VARCHAR(80);
