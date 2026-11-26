import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CollaboratorsProvider, useCollaborators } from './store';
import { CreateCollaboratorForm } from '@/features/create-collaborator';
import * as collaboratorApi from '@/shared/api/collaborator';

vi.mock('@/shared/api/auth', () => ({ getAuthToken: () => 'token-de-teste' }));
vi.mock('@/shared/api/collaborator', () => ({
  listCollaborators: vi.fn(),
  createCollaborator: vi.fn(),
  updateCollaborator: vi.fn(),
  patchCollaborator: vi.fn(),
  deleteCollaborator: vi.fn(),
  listCollaboratorTasks: vi.fn(),
}));

function CollaboratorSummary() {
  const { items } = useCollaborators();
  return (
    <div>
      {items.map((c) => (
        <div key={c.id} data-testid={`collab-${c.id}`}>
          <span>{c.name}</span>
          <span>{c.projects || 'sem-projetos'}</span>
        </div>
      ))}
    </div>
  );
}

describe('Colaboradores (V8 + Correções)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('mapeia nome composto, sobrenome e projetos envolvidos retornados pela API V8', async () => {
    collaboratorApi.listCollaborators.mockResolvedValue([
      {
        id: 10,
        nome: 'Ana Paula',
        sobrenome: 'Silva',
        email: 'ana@faisca.com',
        cargo: 'Arquiteta',
        projetos: 'RES-024, COM-011',
        projetosIds: [1, 2],
        projetosCodigos: ['RES-024', 'COM-011'],
        tarefasIds: [5, 6],
      },
    ]);

    render(
      <CollaboratorsProvider>
        <CollaboratorSummary />
      </CollaboratorsProvider>,
    );

    expect(await screen.findByText('Ana Paula Silva')).toBeInTheDocument();
    expect(screen.getByText('RES-024, COM-011')).toBeInTheDocument();
  });

  it('preserva nome composto na edição e envia nova senha quando informada', async () => {
    collaboratorApi.listCollaborators.mockResolvedValue([]);
    collaboratorApi.updateCollaborator.mockResolvedValue({
      id: 10,
      nome: 'Ana Paula',
      sobrenome: 'Silva',
      email: 'ana@faisca.com',
      cargo: 'Arquiteta Sênior',
      projetos: 'RES-024',
      projetosIds: [1],
      projetosCodigos: ['RES-024'],
      tarefasIds: [5],
    });

    const onClose = vi.fn();
    render(
      <MemoryRouter>
        <CollaboratorsProvider>
          <CreateCollaboratorForm
            onClose={onClose}
            collaborator={{
              id: 10,
              firstName: 'Ana Paula',
              lastName: 'Silva',
              name: 'Ana Paula Silva',
              role: 'Arquiteta',
              email: 'ana@faisca.com',
              projects: 'RES-024',
            }}
          />
        </CollaboratorsProvider>
      </MemoryRouter>,
    );

    expect(screen.getByLabelText('Nome')).toHaveValue('Ana Paula');
    expect(screen.getByLabelText('Sobrenome')).toHaveValue('Silva');

    fireEvent.change(screen.getByLabelText('Cargo'), { target: { value: 'Arquiteta Sênior' } });
    fireEvent.change(screen.getByLabelText('Nova senha'), { target: { value: 'novasenha123' } });
    fireEvent.change(screen.getByLabelText('Confirmar senha'), { target: { value: 'novasenha123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar alterações' }));

    await waitFor(() => {
      expect(collaboratorApi.updateCollaborator).toHaveBeenCalledWith(10, {
        nome: 'Ana Paula',
        sobrenome: 'Silva',
        email: 'ana@faisca.com',
        cargo: 'Arquiteta Sênior',
        senha: 'novasenha123',
      });
    });
    expect(onClose).toHaveBeenCalledOnce();
  });

  it('valida senha mínima de 8 caracteres ao criar colaborador', async () => {
    collaboratorApi.listCollaborators.mockResolvedValue([]);

    render(
      <MemoryRouter>
        <CollaboratorsProvider>
          <CreateCollaboratorForm onClose={vi.fn()} />
        </CollaboratorsProvider>
      </MemoryRouter>,
    );

    fireEvent.change(screen.getByLabelText('Nome'), { target: { value: 'João' } });
    fireEvent.change(screen.getByLabelText('Sobrenome'), { target: { value: 'Souza' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'joao@faisca.com' } });
    fireEvent.change(screen.getByLabelText('Cargo'), { target: { value: 'Engenheiro' } });
    fireEvent.change(screen.getByLabelText('Senha'), { target: { value: '123456' } });
    fireEvent.change(screen.getByLabelText('Confirmar senha'), { target: { value: '123456' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar colaborador' }));

    expect(await screen.findByText('Senha deve ter entre 8 e 72 caracteres.')).toBeInTheDocument();
    expect(collaboratorApi.createCollaborator).not.toHaveBeenCalled();
  });
});
