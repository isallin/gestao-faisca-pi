USE gestao_faisca;

INSERT INTO usuario (nome, sobrenome, email, cargo, senha) VALUES
('Beatriz', 'Martins', 'beatriz.martins@faisca.com.br', 'Arquiteta', '$2a$10$mockhashbeatriz'),
('Camila', 'Ferreira', 'camila.ferreira@faisca.com.br', 'Arquiteta', '$2a$10$mockhashcamila'),
('Rafael', 'Nunes', 'rafael.nunes@faisca.com.br', 'Engenheiro', '$2a$10$mockhashrafael'),
('Luiza', 'Rocha', 'luiza.rocha@faisca.com.br', 'Gestora de Projetos', '$2a$10$mockhashluiza');

INSERT INTO cliente (tipo, nome, email, telefone, observacao, cep, logradouro, numero, complemento, bairro, cidade, estado, pais) VALUES
('PF', 'Isabel Cristina Souza', 'isabel.souza@email.com', '11999887766', 'Cliente residencial de alto padrao.', '01310930', 'Rua Augusta', '1450', 'Apto 82', 'Consolacao', 'Sao Paulo', 'SP', 'Brasil'),
('PF', 'Bruno Almeida Rocha', 'bruno.rocha@email.com', '11977665544', 'Reforma completa de apartamento.', '04567000', 'Rua Gomes de Carvalho', '510', 'Apto 131', 'Vila Olimpia', 'Sao Paulo', 'SP', 'Brasil'),
('PJ', 'Clara Arquitetura Corporativa LTDA', 'contato@claracorp.com.br', '1133557799', 'Projeto comercial para nova sede.', '30140071', 'Av. Afonso Pena', '1200', 'Conj. 901', 'Centro', 'Belo Horizonte', 'MG', 'Brasil'),
('PJ', 'Viva Med Clinica Integrada', 'obra@vivamed.com.br', '1144662288', 'Adequacoes para clinica multidisciplinar.', '80230010', 'Rua Emiliano Perneta', '680', 'Sala 04', 'Centro', 'Curitiba', 'PR', 'Brasil');

INSERT INTO cliente_pf (cliente_id, cpf, rg, data_nascimento, estado_civil, profissao) VALUES
((SELECT id FROM cliente WHERE nome = 'Isabel Cristina Souza'), '12345678901', '334455667', '1986-02-15', 'Casada', 'Administradora'),
((SELECT id FROM cliente WHERE nome = 'Bruno Almeida Rocha'), '10987654321', '776655443', '1990-09-03', 'Solteiro', 'Advogado');

INSERT INTO cliente_pj (cliente_id, cnpj, nome_fantasia) VALUES
((SELECT id FROM cliente WHERE nome = 'Clara Arquitetura Corporativa LTDA'), '44556677000122', 'Clara Corp'),
((SELECT id FROM cliente WHERE nome = 'Viva Med Clinica Integrada'), '22334455000199', 'Viva Med');

INSERT INTO endereco_cliente (cliente_id, cep, logradouro, numero, complemento, bairro, cidade, estado, pais) VALUES
((SELECT id FROM cliente WHERE nome = 'Isabel Cristina Souza'), '01310930', 'Rua Augusta', '1450', 'Apto 82', 'Consolacao', 'Sao Paulo', 'SP', 'Brasil'),
((SELECT id FROM cliente WHERE nome = 'Bruno Almeida Rocha'), '04567000', 'Rua Gomes de Carvalho', '510', 'Apto 131', 'Vila Olimpia', 'Sao Paulo', 'SP', 'Brasil'),
((SELECT id FROM cliente WHERE nome = 'Clara Arquitetura Corporativa LTDA'), '30140071', 'Av. Afonso Pena', '1200', 'Conj. 901', 'Centro', 'Belo Horizonte', 'MG', 'Brasil'),
((SELECT id FROM cliente WHERE nome = 'Viva Med Clinica Integrada'), '80230010', 'Rua Emiliano Perneta', '680', 'Sala 04', 'Centro', 'Curitiba', 'PR', 'Brasil');

INSERT INTO representante_legal (fkClientePJ, nome_completo, cpf, cargo, email, telefone) VALUES
((SELECT cliente_id FROM cliente_pj cpj JOIN cliente c ON c.id = cpj.cliente_id WHERE c.nome = 'Clara Arquitetura Corporativa LTDA'), 'Mariana Carla Duarte', '45612378910', 'Diretora Operacional', 'mariana@claracorp.com.br', '31991112222'),
((SELECT cliente_id FROM cliente_pj cpj JOIN cliente c ON c.id = cpj.cliente_id WHERE c.nome = 'Viva Med Clinica Integrada'), 'Felipe Renan Torres', '74185296300', 'Socio Administrador', 'felipe@vivamed.com.br', '41994443322');

INSERT INTO projeto (fkCliente, codigo, relacao_proprietario, nome, tipo_imovel, area_m2, unidade_numero, condominio, matricula, cep, logradouro, numero, complemento, bairro, cidade, estado, pais, data_inicio, data_fim, observacao, criadoPor, fkTarefaFase) VALUES
((SELECT id FROM cliente WHERE nome = 'Isabel Cristina Souza'), 'RES-024', 'Proprietaria', 'Apartamento Consolacao', 'Residencial', 148.50, '82', 'Edificio Lumina', '12345', '01310930', 'Rua Augusta', '1450', 'Apto 82', 'Consolacao', 'Sao Paulo', 'SP', 'Brasil', '2026-10-01 00:00:00', '2027-03-30 00:00:00', 'Projeto executivo e acompanhamento de obra.', 4, (SELECT idfase FROM fase WHERE nome = 'Executivo')),
((SELECT id FROM cliente WHERE nome = 'Clara Arquitetura Corporativa LTDA'), 'COM-011', 'Diretoria', 'Sede Clara Corp', 'Comercial', 420.00, '901', 'Business Tower', '99881', '30140071', 'Av. Afonso Pena', '1200', 'Conj. 901', 'Centro', 'Belo Horizonte', 'MG', 'Brasil', '2026-09-20 00:00:00', '2027-02-10 00:00:00', 'Atualizacao de layout corporativo.', 4, (SELECT idfase FROM fase WHERE nome = 'Projeto legal')),
((SELECT id FROM cliente WHERE nome = 'Bruno Almeida Rocha'), 'INT-032', 'Proprietario', 'Reforma Vila Olimpia', 'Residencial', 96.00, '131', 'Condominio Horizonte', '55441', '04567000', 'Rua Gomes de Carvalho', '510', 'Apto 131', 'Vila Olimpia', 'Sao Paulo', 'SP', 'Brasil', '2026-11-01 00:00:00', '2027-01-25 00:00:00', 'Foco em marcenaria e automacao.', 1, (SELECT idfase FROM fase WHERE nome = 'Pré-executivo'));

INSERT INTO endereco_projeto (projeto_id, cep, logradouro, numero, complemento, bairro, cidade, estado, pais) VALUES
((SELECT id FROM projeto WHERE codigo = 'RES-024'), '01310930', 'Rua Augusta', '1450', 'Apto 82', 'Consolacao', 'Sao Paulo', 'SP', 'Brasil'),
((SELECT id FROM projeto WHERE codigo = 'COM-011'), '30140071', 'Av. Afonso Pena', '1200', 'Conj. 901', 'Centro', 'Belo Horizonte', 'MG', 'Brasil'),
((SELECT id FROM projeto WHERE codigo = 'INT-032'), '04567000', 'Rua Gomes de Carvalho', '510', 'Apto 131', 'Vila Olimpia', 'Sao Paulo', 'SP', 'Brasil');

INSERT INTO fornecedor_empresa (nome, cidade, origem, estado, ja_trabalhei, observacao) VALUES
('Madeira Fina Marcenaria', 'Sao Paulo', 'Indicação', 'SP', 1, 'Fornecedor homologado para mobiliario sob medida.'),
('Luz Tech Iluminacao', 'Belo Horizonte', 'Feira de Arquitetura', 'MG', 1, 'Especialista em solucoes LED para corporativo.'),
('Acqua Engenharia', 'Curitiba', 'Parceiro', 'PR', 1, 'Equipe de hidraulica e combate a incendio.');

INSERT INTO fornecedor_categ_assoc (pkCategoria, pkFornecedor) VALUES
((SELECT id FROM fornecedor_categoria WHERE nome = 'Marcenaria'), (SELECT id FROM fornecedor_empresa WHERE nome = 'Madeira Fina Marcenaria')),
((SELECT id FROM fornecedor_categoria WHERE nome = 'Iluminação'), (SELECT id FROM fornecedor_empresa WHERE nome = 'Luz Tech Iluminacao')),
((SELECT id FROM fornecedor_categoria WHERE nome = 'Instalações'), (SELECT id FROM fornecedor_empresa WHERE nome = 'Acqua Engenharia'));

INSERT INTO fornecedor_contato (fkFornecedor, nome, email, telefone) VALUES
((SELECT id FROM fornecedor_empresa WHERE nome = 'Madeira Fina Marcenaria'), 'Contato Principal', 'contato@madeirafina.com', '1134569870'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Madeira Fina Marcenaria'), 'Orçamentos', 'orcamento@madeirafina.com', '11977881122'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Luz Tech Iluminacao'), 'Vendas Corporativo', 'vendas@luztech.com.br', '3132448800'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Acqua Engenharia'), 'Comercial', 'comercial@acquaeng.com', '4133226600');

INSERT INTO fornecedor_projeto (fornecedor_id, projeto_id, servico, valor, data_inicio, data_fim) VALUES
((SELECT id FROM fornecedor_empresa WHERE nome = 'Madeira Fina Marcenaria'), (SELECT id FROM projeto WHERE codigo = 'RES-024'), 'Marcenaria sob medida cozinha e sala', 48000.00, '2026-11-15', '2027-01-20'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Madeira Fina Marcenaria'), (SELECT id FROM projeto WHERE codigo = 'INT-032'), 'Painéis e armários planejados', 32500.00, '2026-11-20', '2027-01-10'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Luz Tech Iluminacao'), (SELECT id FROM projeto WHERE codigo = 'COM-011'), 'Projeto luminotécnico LED', 27000.00, '2026-10-10', '2026-12-20'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Acqua Engenharia'), (SELECT id FROM projeto WHERE codigo = 'COM-011'), 'Hidráulica e combate a incêndio', 35000.00, '2026-10-15', '2027-01-15'),
((SELECT id FROM fornecedor_empresa WHERE nome = 'Acqua Engenharia'), (SELECT id FROM projeto WHERE codigo = 'RES-024'), 'Adequação hidráulica', 14200.00, '2026-11-05', '2026-12-15');

INSERT INTO tarefa (fkProjeto, fkTarefaCategoria, fkTarefaFase, nome, prioridade_id, data_inicio_prev, data_fim_prev, data_inicio_real, data_fim_real, observacao, criadoPor) VALUES
((SELECT id FROM projeto WHERE codigo = 'RES-024'), (SELECT id FROM tarefa_categoria WHERE nome = 'Projeto'), (SELECT idfase FROM fase WHERE nome = 'Executivo'), 'Revisar planta de iluminacao', (SELECT id FROM prioridade WHERE nome = 'Alta'), '2026-11-03 00:00:00', '2026-11-07 00:00:00', NULL, NULL, 'Compatibilizar pontos com forro e marcenaria.', 1),
((SELECT id FROM projeto WHERE codigo = 'RES-024'), (SELECT id FROM tarefa_categoria WHERE nome = 'Documentação'), (SELECT idfase FROM fase WHERE nome = 'Executivo'), 'Emitir prancha de marcenaria', (SELECT id FROM prioridade WHERE nome = 'Média'), '2026-11-08 00:00:00', '2026-11-13 00:00:00', NULL, NULL, 'Detalhar modulos da cozinha e area gourmet.', 2),
((SELECT id FROM projeto WHERE codigo = 'COM-011'), (SELECT id FROM tarefa_categoria WHERE nome = 'Documentação'), (SELECT idfase FROM fase WHERE nome = 'Projeto legal'), 'Enviar prancha para prefeitura', (SELECT id FROM prioridade WHERE nome = 'Alta'), '2026-11-10 00:00:00', '2026-11-12 00:00:00', '2026-11-10 09:00:00', '2026-11-11 17:00:00', 'Inclui atualizacao de acessibilidade.', 4),
((SELECT id FROM projeto WHERE codigo = 'COM-011'), (SELECT id FROM tarefa_categoria WHERE nome = 'Compatibilização'), (SELECT idfase FROM fase WHERE nome = 'Projeto legal'), 'Ajustar compatibilizacao estrutural', (SELECT id FROM prioridade WHERE nome = 'Crítica'), '2026-11-12 00:00:00', '2026-11-20 00:00:00', NULL, NULL, 'Pendencia critica para emissao final.', 3),
((SELECT id FROM projeto WHERE codigo = 'INT-032'), (SELECT id FROM tarefa_categoria WHERE nome = 'Projeto'), (SELECT idfase FROM fase WHERE nome = 'Pré-executivo'), 'Aprovar amostras de madeira', (SELECT id FROM prioridade WHERE nome = 'Média'), '2026-11-14 00:00:00', '2026-11-18 00:00:00', NULL, NULL, 'Cliente prefere tom freijo natural.', 2),
((SELECT id FROM projeto WHERE codigo = 'INT-032'), (SELECT id FROM tarefa_categoria WHERE nome = 'Produção'), (SELECT idfase FROM fase WHERE nome = 'Pré-executivo'), 'Montar apresentacao do conceito', (SELECT id FROM prioridade WHERE nome = 'Baixa'), '2026-11-15 00:00:00', '2026-11-22 00:00:00', NULL, NULL, 'Incluir perspectivas da sala e varanda.', 1);

INSERT INTO tarefa_fase (idProjeto, idFase, pkTarefa)
SELECT fkProjeto, fkTarefaFase, id FROM tarefa;

INSERT INTO status (fkTarefa, status) VALUES
((SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), 'Em andamento'),
((SELECT id FROM tarefa WHERE nome = 'Emitir prancha de marcenaria'), 'Pendente'),
((SELECT id FROM tarefa WHERE nome = 'Enviar prancha para prefeitura'), 'Concluído'),
((SELECT id FROM tarefa WHERE nome = 'Ajustar compatibilizacao estrutural'), 'Em andamento'),
((SELECT id FROM tarefa WHERE nome = 'Aprovar amostras de madeira'), 'Pendente'),
((SELECT id FROM tarefa WHERE nome = 'Montar apresentacao do conceito'), 'Pendente');

INSERT INTO colaborador_tarefa (pkTarefa, pkColaborador, data_atribuicao) VALUES
((SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), (SELECT id FROM usuario WHERE email = 'beatriz.martins@faisca.com.br'), '2026-11-01 09:00:00'),
((SELECT id FROM tarefa WHERE nome = 'Emitir prancha de marcenaria'), (SELECT id FROM usuario WHERE email = 'camila.ferreira@faisca.com.br'), '2026-11-02 09:00:00'),
((SELECT id FROM tarefa WHERE nome = 'Enviar prancha para prefeitura'), (SELECT id FROM usuario WHERE email = 'luiza.rocha@faisca.com.br'), '2026-11-05 09:00:00'),
((SELECT id FROM tarefa WHERE nome = 'Ajustar compatibilizacao estrutural'), (SELECT id FROM usuario WHERE email = 'rafael.nunes@faisca.com.br'), '2026-11-06 09:00:00'),
((SELECT id FROM tarefa WHERE nome = 'Aprovar amostras de madeira'), (SELECT id FROM usuario WHERE email = 'camila.ferreira@faisca.com.br'), '2026-11-08 09:00:00'),
((SELECT id FROM tarefa WHERE nome = 'Montar apresentacao do conceito'), (SELECT id FROM usuario WHERE email = 'beatriz.martins@faisca.com.br'), '2026-11-10 09:00:00'),
((SELECT id FROM tarefa WHERE nome = 'Montar apresentacao do conceito'), (SELECT id FROM usuario WHERE email = 'camila.ferreira@faisca.com.br'), '2026-11-15 09:00:00');

INSERT INTO subtarefa (tarefa_id, nome, `check`) VALUES
((SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), 'Conferir pontos com forro de gesso', 1),
((SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), 'Validar circuito de trilhos', 0),
((SELECT id FROM tarefa WHERE nome = 'Ajustar compatibilizacao estrutural'), 'Revisar shaft tecnico', 0),
((SELECT id FROM tarefa WHERE nome = 'Ajustar compatibilizacao estrutural'), 'Aprovar vigas de reforco', 0),
((SELECT id FROM tarefa WHERE nome = 'Aprovar amostras de madeira'), 'Separar opcoes de acabamento', 1);

INSERT INTO tarefa_checklist (fkTarefa, texto, concluido) VALUES
((SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), 'Conferir pontos com forro de gesso', TRUE),
((SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), 'Validar circuito de trilhos', FALSE),
((SELECT id FROM tarefa WHERE nome = 'Ajustar compatibilizacao estrutural'), 'Revisar shaft tecnico', FALSE),
((SELECT id FROM tarefa WHERE nome = 'Ajustar compatibilizacao estrutural'), 'Aprovar vigas de reforco', FALSE),
((SELECT id FROM tarefa WHERE nome = 'Aprovar amostras de madeira'), 'Separar opcoes de acabamento', TRUE);

INSERT INTO registro_horas (fkColaborador, fkTarefa, hora_inicio, hora_fim, ultima_atualizacao, descricao, criadoPor) VALUES
((SELECT id FROM usuario WHERE email = 'beatriz.martins@faisca.com.br'), (SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), '2026-11-03 09:00:00', '2026-11-03 12:00:00', '2026-11-03 12:05:00', 'Revisao inicial da planta.', 1),
((SELECT id FROM usuario WHERE email = 'beatriz.martins@faisca.com.br'), (SELECT id FROM tarefa WHERE nome = 'Revisar planta de iluminacao'), '2026-11-04 14:00:00', '2026-11-04 17:30:00', '2026-11-04 17:35:00', 'Compatibilizacao com marcenaria.', 1),
((SELECT id FROM usuario WHERE email = 'luiza.rocha@faisca.com.br'), (SELECT id FROM tarefa WHERE nome = 'Enviar prancha para prefeitura'), '2026-11-10 08:30:00', '2026-11-10 11:30:00', '2026-11-10 11:32:00', 'Ajuste final e envio.', 4),
((SELECT id FROM usuario WHERE email = 'camila.ferreira@faisca.com.br'), (SELECT id FROM tarefa WHERE nome = 'Montar apresentacao do conceito'), '2026-11-16 10:00:00', '2026-11-16 13:00:00', '2026-11-16 13:10:00', 'Montagem de layout e referencias.', 2);
