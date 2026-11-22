import { render, screen } from '@testing-library/react';
import { vi, test, expect } from 'vitest';
import { ProjectsProvider, useProjects } from './store';
import { listPhases } from '@/shared/api/phase';
import { listProjects } from '@/shared/api/project';

vi.mock('@/shared/api/auth', () => ({ getAuthToken: () => 'token-de-teste' }));
vi.mock('@/shared/api/phase', () => ({ listPhases: vi.fn() }));
vi.mock('@/shared/api/project', () => ({
  listProjects: vi.fn(),
  createProject: vi.fn(),
  updateProject: vi.fn(),
  deleteProject: vi.fn(),
}));

function PhaseList() {
  const { phases } = useProjects();
  return <div>{phases.map((phase) => phase.nome).join(', ')}</div>;
}

test('mantém fases carregadas do banco mesmo se a listagem de projetos falhar', async () => {
  listPhases.mockResolvedValue([{ id: 1, nome: 'Planejamento' }]);
  listProjects.mockRejectedValue(new Error('projetos indisponíveis'));

  render(<ProjectsProvider><PhaseList /></ProjectsProvider>);

  expect(await screen.findByText('Planejamento')).toBeInTheDocument();
  expect(listPhases).toHaveBeenCalledOnce();
});
