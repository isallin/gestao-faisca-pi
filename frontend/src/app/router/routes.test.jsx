import { render, screen } from '@testing-library/react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import { AppProviders } from '../providers/AppProviders';
import { routes } from './routes';

const renderAt = (path) =>
  render(
    <AppProviders>
      <RouterProvider router={createMemoryRouter(routes, { initialEntries: [path] })} />
    </AppProviders>,
  );

describe('Rotas', () => {
  it('redireciona / para o login', () => {
    renderAt('/');
    expect(screen.getByRole('heading', { name: 'Bem-vindo' })).toBeInTheDocument();
  });

  it('renderiza o dashboard dentro do AppShell', () => {
    renderAt('/dashboard');
    expect(screen.getByRole('heading', { name: 'Dashboard' })).toBeInTheDocument();
    expect(screen.getByRole('navigation', { name: 'Menu principal' })).toBeInTheDocument();
  });

  it('mostra 404 para rotas desconhecidas', () => {
    renderAt('/nada');
    expect(screen.getByText('404')).toBeInTheDocument();
  });
});
