import { fireEvent, render, screen } from '@testing-library/react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import { AppProviders } from '@/app/providers/AppProviders';
import { routes } from '@/app/router/routes';

const renderDashboard = () =>
  render(
    <AppProviders>
      <RouterProvider router={createMemoryRouter(routes, { initialEntries: ['/dashboard'] })} />
    </AppProviders>,
  );

describe('Dashboard', () => {
  it('mostra os 4 KPIs e o gráfico empilhado', () => {
    renderDashboard();
    [
      'Tarefas pendentes (hoje)',
      'Tarefas pendentes (semana)',
      'Tarefas atrasadas',
      'Horas trabalhadas no mês',
    ].forEach((label) => expect(screen.getByText(label)).toBeInTheDocument());
    expect(screen.getByRole('heading', { name: 'Tarefas por mês' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /tarefas urgentes/i })).toBeInTheDocument();
  });

  it('aplica o filtro de projeto às informações', () => {
    renderDashboard();
    expect(screen.getByText('de 4 em aberto')).toBeInTheDocument();
    fireEvent.change(screen.getByLabelText('Projeto'), { target: { value: 'COM-011' } });
    expect(screen.getByText('de 1 em aberto')).toBeInTheDocument();
  });

  it('não exibe mais a lista de projetos recentes', () => {
    renderDashboard();
    expect(screen.queryByRole('heading', { name: 'Projetos recentes' })).toBeNull();
  });

  it('o botão Sair fica na lateral e o logo foi removido', () => {
    renderDashboard();
    expect(screen.getByRole('link', { name: 'Sair' })).toBeInTheDocument();
    expect(screen.queryByAltText('Faísca Arquitetura')).toBeNull();
  });
});
