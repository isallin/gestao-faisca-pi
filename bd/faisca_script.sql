CREATE DATABASE gestao_faisca;
USE gestao_faisca;
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(70) NOT NULL,
    sobrenome VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    cargo VARCHAR(45),
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(2) NOT NULL,
    nome VARCHAR(150),
    email VARCHAR(120),
    telefone VARCHAR(11),
    observacao VARCHAR(300)
);

CREATE TABLE cliente_pf (
    cliente_id INT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL,
    rg VARCHAR(20),
    data_nascimento DATE,
    estado_civil VARCHAR(30),
    profissao VARCHAR(80),
    CONSTRAINT fk_cliente_pf FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

CREATE TABLE cliente_pj (
    cliente_id INT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    nome_fantasia VARCHAR(140) NOT NULL,
    CONSTRAINT fk_cliente_pj FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

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

CREATE TABLE fase (
    idfase INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80),
    ordem INT
);

CREATE TABLE projeto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkCliente INT NOT NULL,
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
    fkTarefaFase INT,
    CONSTRAINT fk_projeto_cliente FOREIGN KEY (fkCliente) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_projeto_fase FOREIGN KEY (fkTarefaFase) REFERENCES fase(idfase) ON DELETE SET NULL
);

CREATE TABLE tarefa_categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80)
);

CREATE TABLE tarefa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkProjeto INT NOT NULL,
    fkTarefaCategoria INT NOT NULL,
    fkTarefaFase INT NOT NULL,
    nome VARCHAR(200),
    prioridade VARCHAR(45),
    data_inicio_prev DATETIME,
    data_fim_prev DATETIME,
    data_inicio_real DATETIME,
    data_fim_real DATETIME,
    observacao VARCHAR(300),
    CONSTRAINT fk_tarefa_projeto FOREIGN KEY (fkProjeto) REFERENCES projeto(id) ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_categoria FOREIGN KEY (fkTarefaCategoria) REFERENCES tarefa_categoria(id),
    CONSTRAINT fk_tarefa_fase FOREIGN KEY (fkTarefaFase) REFERENCES fase(idfase)
);

CREATE TABLE status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkTarefa INT NOT NULL,
    status VARCHAR(70),
    CONSTRAINT fk_status_tarefa FOREIGN KEY (fkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

CREATE TABLE registro_horas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fkTarefa INT NOT NULL,
    hora_inicio DATETIME,
    hora_fim DATETIME,
    CONSTRAINT fk_registro_horas_tarefa FOREIGN KEY (fkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE
);

CREATE TABLE colaborador_tarefa (
    pkTarefa INT NOT NULL,
    pkColaborador INT NOT NULL,
    PRIMARY KEY (pkTarefa, pkColaborador),
    CONSTRAINT fk_colaborador_tarefa FOREIGN KEY (pkTarefa) REFERENCES tarefa(id) ON DELETE CASCADE,
    CONSTRAINT fk_colaborador_usuario FOREIGN KEY (pkColaborador) REFERENCES usuario(id) ON DELETE CASCADE
);

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