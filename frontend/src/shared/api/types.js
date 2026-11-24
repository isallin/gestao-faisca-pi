/**
 * Definições de tipos (JSDoc) sincronizadas com a Modelagem de Dados V8 e contratos REST do backend.
 */

/**
 * @typedef {Object} UsuarioV8
 * @property {number} id
 * @property {string} nome
 * @property {string} sobrenome
 * @property {string} email
 * @property {string} cargo
 * @property {string} [projetos]
 * @property {number[]} [projetosIds]
 * @property {string[]} [projetosCodigos]
 * @property {number[]} [tarefasIds]
 */

/**
 * @typedef {Object} EnderecoV8
 * @property {number} [id]
 * @property {number} [clienteId]
 * @property {number} [projetoId]
 * @property {string} [cep]
 * @property {string} [logradouro]
 * @property {string} [numero]
 * @property {string} [complemento]
 * @property {string} [bairro]
 * @property {string} [cidade]
 * @property {string} [estado]
 * @property {string} [pais]
 */

/**
 * @typedef {Object} RepresentanteLegalV8
 * @property {number} [id]
 * @property {number} [clientePjId]
 * @property {string} nomeCompleto
 * @property {string} [cpf]
 * @property {string} [cargo]
 * @property {string} [email]
 * @property {string} [telefone]
 */

/**
 * @typedef {Object} ClienteV8
 * @property {number} id
 * @property {string} name
 * @property {'Pessoa Física'|'Pessoa Jurídica'} type
 * @property {string} projects
 * @property {string} address
 * @property {string} phone
 * @property {string} email
 * @property {string} note
 * @property {Record<string, any>} [details]
 * @property {EnderecoV8[]} [enderecos]
 * @property {RepresentanteLegalV8[]} [representantes]
 */

/**
 * @typedef {Object} FaseV8
 * @property {number} id
 * @property {string} nome
 * @property {number} [ordem]
 */

/**
 * @typedef {Object} PrioridadeV8
 * @property {number} id
 * @property {string} nome
 */

/**
 * @typedef {Object} TarefaCategoriaV8
 * @property {number} id
 * @property {string} nome
 */

/**
 * @typedef {Object} SubtarefaV8
 * @property {number} [id]
 * @property {number} [tarefaId]
 * @property {string} nome
 * @property {boolean} check
 */

/**
 * @typedef {Object} ColaboradorTarefaV8
 * @property {number} colaboradorId
 * @property {string} nome
 * @property {string} sobrenome
 * @property {string} email
 * @property {string} cargo
 * @property {number} tarefaId
 * @property {string} tarefaNome
 * @property {number|null} projetoId
 * @property {string|null} projetoCodigo
 * @property {string|null} dataAtribuicao
 */

/**
 * @typedef {Object} TarefaFaseV8
 * @property {number} projetoId
 * @property {string} projetoCodigo
 * @property {number} faseId
 * @property {string} faseNome
 * @property {number} tarefaId
 * @property {string} tarefaNome
 */

/**
 * @typedef {Object} RegistroHorasV8
 * @property {number} id
 * @property {number} tarefaId
 * @property {number|null} colaboradorId
 * @property {string|null} colaboradorNome
 * @property {string} inicio
 * @property {string} fim
 * @property {string|null} ultimaAtualizacao
 * @property {string} [descricao]
 */

/**
 * @typedef {Object} FornecedorCategoriaV8
 * @property {number} id
 * @property {string} nome
 */

/**
 * @typedef {Object} FornecedorContatoV8
 * @property {number} [id]
 * @property {number} [fornecedorId]
 * @property {string} [nome]
 * @property {string} [email]
 * @property {string} [telefone]
 */

/**
 * @typedef {Object} FornecedorProjetoV8
 * @property {number} fornecedorId
 * @property {string} [fornecedorNome]
 * @property {number} projetoId
 * @property {string} [projetoCodigo]
 * @property {string} [projetoNome]
 * @property {string} [servico]
 * @property {number|null} [valor]
 * @property {string|null} [dataInicio]
 * @property {string|null} [dataFim]
 */

/**
 * @typedef {Object} FornecedorEmpresaV8
 * @property {number} id
 * @property {string} name
 * @property {string} nome
 * @property {string} category
 * @property {string|null} phone
 * @property {string|null} phone2
 * @property {string|null} email
 * @property {string|null} email2
 * @property {string|null} city
 * @property {string|null} cidade
 * @property {string|null} origem
 * @property {string|null} estado
 * @property {boolean} jaTrabalhei
 * @property {string} projects
 * @property {string|null} note
 * @property {string|null} observacao
 * @property {FornecedorCategoriaV8[]} categorias
 * @property {FornecedorContatoV8[]} contatos
 * @property {FornecedorProjetoV8[]} projetosVinculados
 */

export {};
