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
const select = (label, value) => fireEvent.change(screen.getByLabelText(label), { target: { value } });

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
    select('Projeto', 'COM-011');
    const list = document.querySelector('.task-list');
    expect(within(list).queryByText('Revisar planta de iluminação')).toBeNull();
    expect(within(list).getByText('Atualizar orçamento geral')).toBeInTheDocument();
    select('Prioridade', 'Alta');
    expect(within(list).queryByText('Atualizar orçamento geral')).toBeNull();
    expect(within(list).getByText('Enviar prancha para prefeitura')).toBeInTheDocument();
  });

  it('mostra o código no calendário e abre o detalhe com registro de tempo', () => {
    renderAt('/tarefa');
    fireEvent.click(screen.getByRole('button', { name: 'RES-024 — Revisar planta de iluminação' }));
    const dialog = screen.getByRole('dialog');
    expect(within(dialog).getByText(/Tempo dedicado: 0 h/)).toBeInTheDocument();
    fireEvent.change(within(dialog).getByLabelText('Data'), { target: { value: '2026-08-04' } });
    fireEvent.change(within(dialog).getByLabelText('Horas'), { target: { value: '2' } });
    fireEvent.click(within(dialog).getByRole('button', { name: /Registrar tempo/ }));
    expect(within(dialog).getByText(/Tempo dedicado: 2 h/)).toBeInTheDocument();
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
  it('Clientes: filtra por tipo e por projeto ativo', () => {
    renderAt('/clientes');
    openFilters();
    select('Tipo de cliente', 'Pessoa Jurídica');
    expect(screen.queryByText('Renata Siqueira')).toBeNull();
    expect(screen.getByText('Ateliê Aurora Ltda.')).toBeInTheDocument();
    select('Tipo de cliente', 'all');
    select('Projeto ativo', 'yes');
    expect(screen.queryByText('Renata Siqueira')).toBeNull(); // INT-032 está "Aguardando"
    expect(screen.queryByText('Grupo Horizonte')).toBeNull(); // COR-008 concluído
    expect(screen.getByText('Marina e Lucas Almeida')).toBeInTheDocument();
  });

  it('Projetos: filtra concluídos; cliente só na visão expandida', () => {
    renderAt('/projetos');
    expect(screen.queryByText('Cliente')).toBeNull();
    openFilters();
    select('Status', 'Concluído');
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

  it('Cliente PJ tem campo de observação', () => {
    renderAt('/add-cliente-juridica');
    expect(screen.getByLabelText('Observação')).toBeInTheDocument();
  });
});
