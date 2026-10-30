import { fireEvent, render, screen, within } from '@testing-library/react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import { sortTasks } from '@/entities/task';
import { AppProviders } from '../providers/AppProviders';
import { routes } from './routes';

const renderAt = (path) =>
  render(
    <AppProviders>
      <RouterProvider router={createMemoryRouter(routes, { initialEntries: [path] })} />
    </AppProviders>,
  );

const openFilters = () => fireEvent.click(screen.getByRole('button', { name: /filtrar/i }));

// Filtros de múltipla escolha (checkboxes): abre o filtro, marca só os valores pedidos e fecha.
const filterGroup = (label) => {
  fireEvent.click(
    screen.getByRole('button', { name: new RegExp(`^${label.replace('?', '\\?')} `) }),
  );
  return screen.getByRole('group', { name: label });
};
const closeFilter = () => fireEvent.mouseDown(document.body);
const choose = (label, ...values) => {
  const group = filterGroup(label);
  const all = within(group).getByRole('checkbox', { name: '(Selecionar tudo)' });
  for (let i = 0; i < 2 && (all.checked || all.indeterminate); i += 1) fireEvent.click(all);
  values.forEach((value) => fireEvent.click(within(group).getByRole('checkbox', { name: value })));
  closeFilter();
};
const clearFilter = (label) => {
  const group = filterGroup(label);
  const all = within(group).getByRole('checkbox', { name: '(Selecionar tudo)' });
  if (!all.checked) fireEvent.click(all);
  closeFilter();
};

describe('Tarefas', () => {
  it('ordena por data e, em empate, por prioridade', () => {
    const out = sortTasks([
      { id: 1, start: '2026-08-05', priority: 'Baixa' },
      { id: 2, start: '2026-08-03', priority: 'Média' },
      { id: 3, start: '2026-08-03', priority: 'Crítica' },
    ]);
    expect(out.map((t) => t.id)).toEqual([3, 2, 1]);
  });

  it('filtra por projeto e prioridade', () => {
    renderAt('/tarefa');
    openFilters();
    choose('Projeto', 'COM-011');
    const list = document.querySelector('.task-list');
    expect(within(list).queryByText('Revisar planta de iluminação')).toBeNull();
    expect(within(list).getByText('Atualizar orçamento geral')).toBeInTheDocument();
    choose('Prioridade', 'Alta');
    expect(within(list).queryByText('Atualizar orçamento geral')).toBeNull();
    expect(within(list).getByText('Enviar prancha para prefeitura')).toBeInTheDocument();
  });

  it('mostra o código no calendário e abre o detalhe com registro de tempo', () => {
    renderAt('/tarefa');
    fireEvent.click(screen.getByRole('button', { name: 'RES-024 — Revisar planta de iluminação' }));
    const dialog = screen.getByRole('dialog');
    // Tela de detalhes: sem formulário, com o botão para registrar tempo
    expect(within(dialog).getByText(/Tempo dedicado: 0 h/)).toBeInTheDocument();
    expect(within(dialog).queryByLabelText('Data/hora início')).toBeNull();
    fireEvent.click(within(dialog).getByRole('button', { name: /\+ Registrar tempo/ }));
    // Tela de cadastro
    fireEvent.change(within(dialog).getByLabelText('Data/hora início'), {
      target: { value: '2026-08-04T09:00' },
    });
    fireEvent.change(within(dialog).getByLabelText('Data/hora fim'), {
      target: { value: '2026-08-04T11:30' },
    });
    fireEvent.change(within(dialog).getByLabelText('Descrição'), {
      target: { value: 'Revisão dos pontos' },
    });
    fireEvent.click(within(dialog).getByRole('button', { name: 'Salvar' }));
    // Volta aos detalhes com o histórico atualizado
    expect(within(dialog).getByText(/Tempo dedicado: 2,5 h/)).toBeInTheDocument();
    expect(within(dialog).getByText('Revisão dos pontos')).toBeInTheDocument();
  });

  it('valida o registro de tempo e permite cancelar', () => {
    renderAt('/tarefa');
    fireEvent.click(screen.getByRole('button', { name: 'RES-024 — Revisar planta de iluminação' }));
    const dialog = screen.getByRole('dialog');
    fireEvent.click(within(dialog).getByRole('button', { name: /\+ Registrar tempo/ }));
    fireEvent.click(within(dialog).getByRole('button', { name: 'Salvar' }));
    expect(within(dialog).getByText('Informe a data/hora de início e de fim.')).toBeInTheDocument();
    fireEvent.change(within(dialog).getByLabelText('Data/hora início'), {
      target: { value: '2026-08-04T11:00' },
    });
    fireEvent.change(within(dialog).getByLabelText('Data/hora fim'), {
      target: { value: '2026-08-04T09:00' },
    });
    fireEvent.click(within(dialog).getByRole('button', { name: 'Salvar' }));
    expect(
      within(dialog).getByText('A data/hora de fim deve ser posterior à de início.'),
    ).toBeInTheDocument();
    fireEvent.click(within(dialog).getByRole('button', { name: 'Cancelar' }));
    expect(within(dialog).getByRole('button', { name: /\+ Registrar tempo/ })).toBeInTheDocument();
    expect(within(dialog).getByText(/Tempo dedicado: 0 h/)).toBeInTheDocument();
  });

  it('Registros: lista os tempos registrados e filtra por projeto', () => {
    renderAt('/tarefa');
    const register = (name, start, end, note) => {
      fireEvent.click(screen.getByRole('button', { name }));
      const dialog = screen.getByRole('dialog');
      fireEvent.click(within(dialog).getByRole('button', { name: /\+ Registrar tempo/ }));
      fireEvent.change(within(dialog).getByLabelText('Data/hora início'), {
        target: { value: start },
      });
      fireEvent.change(within(dialog).getByLabelText('Data/hora fim'), { target: { value: end } });
      fireEvent.change(within(dialog).getByLabelText('Descrição'), { target: { value: note } });
      fireEvent.click(within(dialog).getByRole('button', { name: 'Salvar' }));
      fireEvent.click(within(dialog).getByRole('button', { name: 'Fechar' }));
    };
    register(
      'RES-024 — Revisar planta de iluminação',
      '2026-08-04T09:00',
      '2026-08-04T11:30',
      'Revisão dos pontos',
    );
    register(
      'COM-011 — Atualizar orçamento geral',
      '2026-08-10T14:00',
      '2026-08-10T15:00',
      'Ajuste de valores',
    );

    fireEvent.click(
      within(screen.getByRole('navigation', { name: 'Menu principal' })).getByRole('link', {
        name: 'Registros',
      }),
    );
    const list = screen.getByRole('region', { name: 'Lista de Registros' });
    expect(within(list).getByText('Revisar planta de iluminação')).toBeInTheDocument();
    expect(within(list).getByText('Atualizar orçamento geral')).toBeInTheDocument();
    expect(within(list).getByText('2,5 h')).toBeInTheDocument();

    openFilters();
    choose('Projeto', 'COM-011');
    expect(within(list).queryByText('Revisar planta de iluminação')).toBeNull();
    expect(within(list).getByText('Atualizar orçamento geral')).toBeInTheDocument();
    fireEvent.click(within(list).getByRole('button', { name: /Ver mais/ }));
    expect(within(list).getByText('Ajuste de valores')).toBeInTheDocument();
  });

  it('Registros: avisa quando não há tempos registrados', () => {
    renderAt('/registros');
    expect(screen.getByText('Nenhum registro de tempo encontrado.')).toBeInTheDocument();
  });

  it('menu lateral: Registros após Tarefas e Colaboradores por último', () => {
    renderAt('/dashboard');
    const labels = within(screen.getByRole('navigation', { name: 'Menu principal' }))
      .getAllByRole('link')
      .map((a) => a.textContent);
    expect(labels).toEqual([
      'Dashboard',
      'Tarefas',
      'Registros',
      'Clientes',
      'Fornecedores',
      'Projetos',
      'Colaboradores',
    ]);
  });

  it('modo Lista usa os cards padrão e permite editar', () => {
    renderAt('/tarefa');
    fireEvent.click(screen.getByRole('tab', { name: 'Lista' }));
    expect(screen.getByRole('region', { name: 'Lista de Tarefas' })).toBeInTheDocument();
    fireEvent.click(screen.getAllByRole('button', { name: /Editar/ })[0]);
    expect(screen.getByRole('heading', { name: 'Editar Tarefa' })).toBeInTheDocument();
  });

  it('formulário permite adicionar itens de checklist', () => {
    renderAt('/add-tarefa');
    fireEvent.click(screen.getByRole('button', { name: /Adicionar item/ }));
    expect(screen.getByLabelText('Item 1 do checklist')).toBeInTheDocument();
  });
});

describe('Listas', () => {
  it('Tarefas: permite marcar mais de uma prioridade no mesmo filtro', () => {
    renderAt('/tarefa');
    openFilters();
    const list = document.querySelector('.task-list');
    choose('Prioridade', 'Alta', 'Crítica');
    expect(within(list).getByText('Revisar planta de iluminação')).toBeInTheDocument(); // Alta
    expect(within(list).getByText('Conferir medição da marcenaria')).toBeInTheDocument(); // Crítica
    expect(within(list).queryByText('Aprovar amostras de madeira')).toBeNull(); // Média
    expect(within(list).queryByText('Montar apresentação do conceito')).toBeNull(); // Baixa
    expect(screen.getByRole('button', { name: /^Prioridade Crítica, Alta$/ })).toBeInTheDocument();
    clearFilter('Prioridade');
    expect(within(list).getByText('Aprovar amostras de madeira')).toBeInTheDocument();
  });

  it('Filtros: desmarcar tudo não mostra nada e "Selecionar tudo" restaura', () => {
    renderAt('/projetos');
    openFilters();
    const group = filterGroup('Status');
    fireEvent.click(within(group).getByRole('checkbox', { name: '(Selecionar tudo)' }));
    expect(screen.getByText(/Nenhum\(a\) projetos encontrado\(a\)/)).toBeInTheDocument();
    fireEvent.click(within(group).getByRole('checkbox', { name: '(Selecionar tudo)' }));
    expect(screen.getByText('Casa Paineiras')).toBeInTheDocument();
  });

  it('Clientes: filtra por tipo e por projeto ativo', () => {
    renderAt('/clientes');
    openFilters();
    choose('Tipo de cliente', 'Pessoa Jurídica');
    expect(screen.queryByText('Renata Siqueira')).toBeNull();
    expect(screen.getByText('Ateliê Aurora Ltda.')).toBeInTheDocument();
    clearFilter('Tipo de cliente');
    choose('Projeto ativo', 'Com projeto ativo');
    expect(screen.queryByText('Renata Siqueira')).toBeNull(); // INT-032 está "Aguardando"
    expect(screen.queryByText('Grupo Horizonte')).toBeNull(); // COR-008 concluído
    expect(screen.getByText('Marina e Lucas Almeida')).toBeInTheDocument();
  });

  it('Fornecedores: filtra por categoria, cidade, projeto e "já trabalhei"', () => {
    renderAt('/fornecedores');
    openFilters();

    choose('Categoria', 'Marcenaria', 'Iluminação');
    expect(screen.getByText('Madeira Viva')).toBeInTheDocument();
    expect(screen.getByText('Lume Iluminação')).toBeInTheDocument();
    expect(screen.queryByText('Pedra & Forma')).toBeNull();
    clearFilter('Categoria');

    choose('Cidade', 'Cotia', 'Campinas');
    expect(screen.getByText('Pedra & Forma')).toBeInTheDocument();
    expect(screen.getByText('Verde Traço')).toBeInTheDocument();
    expect(screen.queryByText('Madeira Viva')).toBeNull();
    clearFilter('Cidade');

    choose('Já trabalhei?', 'Não');
    expect(screen.getByText('Verde Traço')).toBeInTheDocument();
    expect(screen.queryByText('Madeira Viva')).toBeNull();
    clearFilter('Já trabalhei?');

    choose('Projeto', 'RES-024');
    expect(screen.getByText('Madeira Viva')).toBeInTheDocument();
    expect(screen.getByText('Pedra & Forma')).toBeInTheDocument();
    expect(screen.queryByText('Lume Iluminação')).toBeNull();

    // filtros diferentes se combinam (E): RES-024 + categoria Marmoraria
    choose('Categoria', 'Marmoraria');
    expect(screen.getByText('Pedra & Forma')).toBeInTheDocument();
    expect(screen.queryByText('Madeira Viva')).toBeNull();
  });

  it('Projetos: filtra concluídos; cliente só na visão expandida', () => {
    renderAt('/projetos');
    expect(screen.queryByText('Cliente')).toBeNull();
    openFilters();
    choose('Status', 'Concluído');
    expect(screen.getByText('Corporativo Horizonte')).toBeInTheDocument();
    expect(screen.queryByText('Casa Paineiras')).toBeNull();
    fireEvent.click(screen.getByRole('button', { name: /Ver mais/ }));
    expect(screen.getByText('Cliente')).toBeInTheDocument();
  });

  it('Colaboradores: "Projetos envolvidos" não duplica ao expandir', () => {
    renderAt('/colaborador');
    fireEvent.click(screen.getAllByRole('button', { name: /Ver mais/ })[0]);
    expect(screen.getAllByText('Projetos envolvidos')).toHaveLength(4); // só os 4 cabeçalhos dos cards
  });

  it('Editar cliente abre o formulário preenchido e salva', () => {
    renderAt('/clientes');
    fireEvent.click(screen.getAllByRole('button', { name: /Editar/ })[0]);
    expect(screen.getByRole('heading', { name: 'Editar Cliente' })).toBeInTheDocument();
    const name = screen.getByLabelText('Nome');
    expect(name).toHaveValue('Marina');
    fireEvent.change(name, { target: { value: 'Marcela' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar alterações' }));
    expect(screen.getByText('Marcela e Lucas Almeida')).toBeInTheDocument();
  });
});

describe('Cadastros', () => {
  it('Novo Colaborador: título e placeholders', () => {
    renderAt('/add-colaborador');
    expect(screen.getByRole('heading', { name: 'Novo Colaborador' })).toBeInTheDocument();
    expect(screen.getByLabelText('Nome')).toHaveAttribute('placeholder');
  });

  it('Cadastro de cliente tem o campo Bairro e mostra no endereço', () => {
    renderAt('/add-cliente-pessoa-fisica');
    expect(screen.getByLabelText('Bairro')).toHaveAttribute('placeholder');
    fireEvent.change(screen.getByLabelText('Nome'), { target: { value: 'Paula' } });
    fireEvent.change(screen.getByLabelText('Logradouro'), { target: { value: 'Rua Harmonia' } });
    fireEvent.change(screen.getByLabelText('Número'), { target: { value: '74' } });
    fireEvent.change(screen.getByLabelText('Bairro'), { target: { value: 'Vila Madalena' } });
    fireEvent.change(screen.getByLabelText('Cidade'), { target: { value: 'São Paulo' } });
    fireEvent.change(screen.getByLabelText('Estado'), { target: { value: 'SP' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar cliente' }));
    fireEvent.click(screen.getAllByRole('button', { name: /Ver mais/ })[0]);
    expect(screen.getByText('Rua Harmonia, 74 — Vila Madalena, São Paulo-SP')).toBeInTheDocument();
  });

  it('Cadastro de projeto tem o campo Bairro e mostra no local', () => {
    renderAt('/add-projeto');
    expect(screen.getByLabelText('Bairro')).toHaveAttribute('placeholder');
    fireEvent.change(screen.getByLabelText('Nome'), { target: { value: 'Loft Centro' } });
    fireEvent.change(screen.getByLabelText('Bairro'), { target: { value: 'Pinheiros' } });
    fireEvent.change(screen.getByLabelText('Cidade'), { target: { value: 'São Paulo' } });
    fireEvent.change(screen.getByLabelText('Estado'), { target: { value: 'SP' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar projeto' }));
    fireEvent.click(screen.getAllByRole('button', { name: /Ver mais/ })[0]);
    expect(screen.getByText('Pinheiros, São Paulo-SP')).toBeInTheDocument();
  });

  it('Cliente PJ tem campo de observação', () => {
    renderAt('/add-cliente-juridica');
    expect(screen.getByLabelText('Observação')).toBeInTheDocument();
  });
});
