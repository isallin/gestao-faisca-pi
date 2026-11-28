import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CreateTaskForm } from './CreateTaskForm';

const addTaskMock = vi.fn();
const updateTaskMock = vi.fn();
const reloadCollaboratorsMock = vi.fn();

vi.mock('@/entities/collaborator', () => ({
  useCollaborators: () => ({
    items: [{ id: 10, name: 'Beatriz Martins' }],
    reload: reloadCollaboratorsMock,
  }),
}));

vi.mock('@/entities/project', () => ({
  useProjects: () => ({
    items: [{ id: 1, code: 'RES-024', clientId: 5 }],
  }),
}));

vi.mock('@/entities/task', () => ({
  TASK_PRIORITIES: ['Baixa', 'Moderada', 'Média', 'Alta', 'Crítica'],
  useTasks: () => ({
    categories: [{ id: 2, nome: 'Projeto' }],
    phases: [{ id: 3, nome: 'Executivo' }],
    priorities: [{ id: 4, nome: 'Alta' }],
    add: addTaskMock,
    update: updateTaskMock,
  }),
}));

function fillTaskForm(overrides = {}) {
  const values = {
    Responsável: 'Beatriz Martins',
    Nome: 'Revisar planta elétrica',
    Projeto: 'RES-024',
    Categoria: 'Projeto',
    Fase: '3',
    Status: 'Pendente',
    Prioridade: 'Alta',
    'Data de início': '2026-12-01',
    'Data do fim': '2026-12-05',
    ...overrides,
  };

  Object.entries(values).forEach(([label, value]) => {
    fireEvent.change(screen.getByLabelText(label), { target: { value } });
  });
}

describe('CreateTaskForm — obrigatoriedade do campo Responsável', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    addTaskMock.mockResolvedValue({ id: 42 });
  });

  it('impede o envio e exibe alerta padrão quando Responsável não é preenchido', async () => {
    render(<CreateTaskForm onClose={vi.fn()} />);

    fillTaskForm({ Responsável: '' });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar tarefa' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('Selecione um responsável para continuar.');
    expect(addTaskMock).not.toHaveBeenCalled();
  });

  it('salva tarefa normalmente quando Responsável é selecionado', async () => {
    const onClose = vi.fn();
    render(<CreateTaskForm onClose={onClose} />);

    fillTaskForm();
    fireEvent.click(screen.getByRole('button', { name: 'Salvar tarefa' }));

    await waitFor(() => expect(addTaskMock).toHaveBeenCalledTimes(1));
    expect(addTaskMock).toHaveBeenCalledWith(
      expect.objectContaining({
        nome: 'Revisar planta elétrica',
        colaboradorId: 10,
        colaboradoresIds: [10],
      }),
    );
    expect(onClose).toHaveBeenCalledTimes(1);
  });
});
