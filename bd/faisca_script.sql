CREATE DATABASE IF NOT EXISTS gestao_faisca;
USE gestao_faisca;

-- ============================================================================
-- MODELAGEM V8 — Gestão Faísca (22 tabelas V8 + integração Google Calendar)
-- ============================================================================

-- 1. usuario
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(70) NOT NULL,
    sobrenome VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    cargo VARCHAR(45),
    senha VARCHAR(255) NOT NULL
);

-- 2. cliente (V8: inclui colunas de endereço sincronizadas com endereco_cliente)
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(2) NOT NULL,
    nome VARCHAR(150),
    email VARCHAR(120),
    telefone VARCHAR(11),
    observacao VARCHAR(300),
    cep VARCHAR(8),
    logradouro VARCHAR(200),
    numero VARCHAR(10),
    complemento VARCHAR(60),
    bairro VARCHAR(80),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    pais VARCHAR(80)
);

-- 3. cliente_pf (1:1 com cliente)
CREATE TABLE cliente_pf (
    cliente_id INT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL,
    rg VARCHAR(20),
    data_nascimento DATE,
    estado_civil VARCHAR(30),
    profissao VARCHAR(80),
    CONSTRAINT fk_cliente_pf FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

-- 4. cliente_pj (1:1 com cliente)
CREATE TABLE cliente_pj (
    cliente_id INT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    nome_fantasia VARCHAR(140) NOT NULL,
    CONSTRAINT fk_cliente_pj FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

-- 5. endereco_cliente (1:N / 1:1 com cliente)
CREATE TABLE endereco_cliente (
    idendereco_projeto INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    cep VARCHAR(8),
    logradouro VARCHAR(200),
    numero VARCHAR(10),
    complemento VARCHAR(60),
    bairro VARCHAR(80),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    pais VARCHAR(80),
    CONSTRAINT fk_endereco_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

-- 6. representante_legal (N:1 com cliente_pj)
CREATE TABLE representante_legal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkClientePJ INT NOT NULL,
    nome_completo VARCHAR(200) NOT NULL,
    cpf VARCHAR(11),
    cargo VARCHAR(80),
    email VARCHAR(120),
    telefone VARCHAR(11),
    CONSTRAINT fk_representante_cliente_pj FOREIGN KEY (fkClientePJ) REFERENCES cliente_pj(cliente_id) ON DELETE CASCADE
);

-- 7. fase
CREATE TABLE fase (
    idfase INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80),
    ordem INT
);

-- 8. projeto (V8: inclui coluna numero + fkCliente + fkTarefaFase)
CREATE TABLE projeto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkCliente INT NOT NULL,
    fkTarefaFase INT,
    codigo VARCHAR(45) NOT NULL,
    relacao_proprietario VARCHAR(80),
    nome VARCHAR(150) NOT NULL,
    tipo_imovel VARCHAR(45),
    area_m2 DECIMAL(10,2),
    unidade_numero VARCHAR(20),
    condominio VARCHAR(100),
    matricula VARCHAR(50),
    data_inicio DATETIME,
    data_fim DATETIME,
    observacao VARCHAR(300),
    cep VARCHAR(8),
    logradouro VARCHAR(200),
    numero VARCHAR(10),
    complemento VARCHAR(60),
    bairro VARCHAR(80),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    pais VARCHAR(80),
    criadoPor INT NULL,
    CONSTRAINT fk_projeto_cliente FOREIGN KEY (fkCliente) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_projeto_fase FOREIGN KEY (fkTarefaFase) REFERENCES fase(idfase) ON DELETE SET NULL
);

-- 9. endereco_projeto (V8: 1:1 dedicado com projeto)
CREATE TABLE endereco_projeto (
    idendereco_projeto INT AUTO_INCREMENT PRIMARY KEY,
    projeto_id INT NOT NULL UNIQUE,
    cep VARCHAR(8),
    logradouro VARCHAR(200),
    numero VARCHAR(10),
    complemento VARCHAR(60),
    bairro VARCHAR(80),
    cidade VARCHAR(80),
    estado VARCHAR(2),
    pais VARCHAR(80),
    CONSTRAINT fk_endereco_projeto_projeto FOREIGN KEY (projeto_id) REFERENCES projeto(id) ON DELETE CASCADE
);

-- 10. tarefa_categoria
CREATE TABLE tarefa_categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80)
);

-- 11. prioridade (V8)
CREATE TABLE prioridade (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(45) NOT NULL
);

-- 12. tarefa (V8: referência normalizada prioridade_id -> prioridade.id)
CREATE TABLE tarefa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkProjeto INT NOT NULL,
    fkTarefaCategoria INT NOT NULL,
    fkTarefaFase INT NOT NULL,
    nome VARCHAR(200),
    prioridade_id INT NOT NULL,
    data_inicio_prev DATETIME,
    data_fim_prev DATETIME,
    data_inicio_real DATETIME,
    data_fim_real DATETIME,
    observacao VARCHAR(300),
    criadoPor INT NULL,
    CONSTRAINT fk_tarefa_projeto FOREIGN KEY (fkProjeto) REFERENCES projeto(id) ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_categoria FOREIGN KEY (fkTarefaCategoria) REFERENCES tarefa_categoria(id),
    CONSTRAINT fk_tarefa_fase FOREIGN KEY (fkTarefaFase) REFERENCES fase(idfase),
    CONSTRAINT fk_tarefa_prioridade FOREIGN KEY (prioridade_id) REFERENCES prioridade(id)
);

-- 13. tarefa_fase (V8: associação projeto <-> fase <-> tarefa)
CREATE TABLE tarefa_fase (
    idProjeto INT NOT NULL,
    idFase INT NOT NULL,
    pkTarefa INT NOT NULL,
    PRIMARY KEY (idProjeto, idFase, pkTarefa),
    CONSTRAINT fk_tarefa_fase_projeto FOREIGN KEY (idProjeto) REFERENCES projeto(id) ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_fase_fase FOREIGN KEY (idFase) REFERENCES fase(idfase),
    CONSTRAINT fk_tarefa_fase_tarefa FOREIGN KEY (pkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

-- 14. status (N:1 com tarefa)
CREATE TABLE status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkTarefa INT NOT NULL,
    status VARCHAR(70),
    CONSTRAINT fk_status_tarefa FOREIGN KEY (fkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

-- 15. subtarefa (V8: 1:N com tarefa)
CREATE TABLE subtarefa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tarefa_id INT NOT NULL,
    nome VARCHAR(200) NOT NULL,
    `check` TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_subtarefa_tarefa FOREIGN KEY (tarefa_id) REFERENCES tarefa(id) ON DELETE CASCADE
);

-- Tabela legada mantida em sincronia com subtarefa para retrocompatibilidade
CREATE TABLE tarefa_checklist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkTarefa INT NOT NULL,
    texto VARCHAR(200) NOT NULL,
    concluido BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_checklist_tarefa FOREIGN KEY (fkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

-- 16. colaborador_tarefa (V8: N:N usuario <-> tarefa com data_atribuicao)
CREATE TABLE colaborador_tarefa (
    pkColaborador INT NOT NULL,
    pkTarefa INT NOT NULL,
    data_atribuicao DATETIME NULL,
    PRIMARY KEY (pkColaborador, pkTarefa),
    CONSTRAINT fk_colaborador_usuario FOREIGN KEY (pkColaborador) REFERENCES usuario(id),
    CONSTRAINT fk_colaborador_tarefa FOREIGN KEY (pkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

-- 17. registro_horas (V8: vinculado a colaborador_tarefa + ultima_atualizacao)
CREATE TABLE registro_horas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkColaborador INT NOT NULL,
    fkTarefa INT NOT NULL,
    hora_inicio DATETIME,
    hora_fim DATETIME,
    ultima_atualizacao DATETIME,
    descricao VARCHAR(300),
    criadoPor INT NULL,
    CONSTRAINT fk_registro_horas_colaborador_tarefa
        FOREIGN KEY (fkColaborador, fkTarefa) REFERENCES colaborador_tarefa(pkColaborador, pkTarefa),
    CONSTRAINT fk_registro_horas_tarefa FOREIGN KEY (fkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

-- 18. fornecedor_empresa (V8)
CREATE TABLE fornecedor_empresa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cidade VARCHAR(80),
    origem VARCHAR(80),
    estado CHAR(2),
    ja_trabalhei TINYINT(1) DEFAULT 0,
    observacao VARCHAR(300)
);

-- 19. fornecedor_categoria (V8)
CREATE TABLE fornecedor_categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL
);

-- 20. fornecedor_categ_assoc (V8: N:N fornecedor_empresa <-> fornecedor_categoria)
CREATE TABLE fornecedor_categ_assoc (
    pkCategoria INT NOT NULL,
    pkFornecedor INT NOT NULL,
    PRIMARY KEY (pkCategoria, pkFornecedor),
    CONSTRAINT fk_fornecedor_categ_assoc_categoria
        FOREIGN KEY (pkCategoria) REFERENCES fornecedor_categoria(id) ON DELETE CASCADE,
    CONSTRAINT fk_fornecedor_categ_assoc_fornecedor
        FOREIGN KEY (pkFornecedor) REFERENCES fornecedor_empresa(id) ON DELETE CASCADE
);

-- 21. fornecedor_contato (V8: 1:N com fornecedor_empresa)
CREATE TABLE fornecedor_contato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkFornecedor INT NOT NULL,
    nome VARCHAR(150),
    email VARCHAR(120),
    telefone VARCHAR(15),
    CONSTRAINT fk_fornecedor_contato_empresa
        FOREIGN KEY (fkFornecedor) REFERENCES fornecedor_empresa(id) ON DELETE CASCADE
);

-- 22. fornecedor_projeto (V8: N:N fornecedor_empresa <-> projeto com servico, valor, datas)
CREATE TABLE fornecedor_projeto (
    fornecedor_id INT NOT NULL,
    projeto_id INT NOT NULL,
    servico VARCHAR(200),
    valor DECIMAL(12,2),
    data_inicio DATE,
    data_fim DATE,
    PRIMARY KEY (fornecedor_id, projeto_id),
    CONSTRAINT fk_fornecedor_projeto_fornecedor
        FOREIGN KEY (fornecedor_id) REFERENCES fornecedor_empresa(id) ON DELETE CASCADE,
    CONSTRAINT fk_fornecedor_projeto_projeto
        FOREIGN KEY (projeto_id) REFERENCES projeto(id) ON DELETE CASCADE
);

-- Tabelas de integração Google Calendar (preservadas)
CREATE TABLE google_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkUsuario INT NOT NULL UNIQUE,
    access_token VARCHAR(2048),
    refresh_token VARCHAR(2048),
    expira_em BIGINT,
    CONSTRAINT fk_google_token_usuario FOREIGN KEY (fkUsuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE google_event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    fkOrigem INT NOT NULL,
    fkUsuario INT NOT NULL,
    google_event_id VARCHAR(255) NOT NULL,
    UNIQUE KEY uq_google_event (tipo, fkOrigem, fkUsuario),
    CONSTRAINT fk_google_event_usuario FOREIGN KEY (fkUsuario) REFERENCES usuario(id) ON DELETE CASCADE
);

-- ============================================================================
-- SEEDS INICIAIS V8
-- ============================================================================

INSERT INTO fase (nome, ordem) VALUES
('Pré-contrato', 1),
('Levantamento', 2),
('Estudo preliminar', 3),
('Estudo de anteprojeto', 4),
('Projeto legal', 5),
('Pré-executivo', 6),
('Executivo', 7),
('Pré-obra', 8),
('Obra', 9);

INSERT INTO tarefa_categoria (nome) VALUES
('Organização'),
('Projeto'),
('Compatibilização'),
('Modelagem'),
('Documentação'),
('Produção'),
('Estudo'),
('Entrega'),
('Administrativo'),
('Financeiro'),
('Outros');

INSERT INTO prioridade (nome) VALUES
('Baixa'),
('Moderada'),
('Média'),
('Alta'),
('Crítica');

INSERT INTO fornecedor_categoria (nome) VALUES
('Marcenaria'),
('Iluminação'),
('Marmoraria'),
('Paisagismo'),
('Construção'),
('Instalações'),
('Materiais'),
('Outros');
