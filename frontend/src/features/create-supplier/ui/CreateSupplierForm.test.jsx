import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CreateSupplierForm } from './CreateSupplierForm';

const addSupplierMock = vi.fn();
const updateSupplierMock = vi.fn();

vi.mock('@/entities/project', () => ({
  useProjects: () => ({
    items: [{ id: 1, code: 'RES-024' }],
  }),
}));

vi.mock('@/entities/supplier', () => ({
  useSuppliers: () => ({
    add: addSupplierMock,
    update: updateSupplierMock,
    categories: [{ id: 1, nome: 'Marcenaria' }],
  }),
}));

function fillSupplierForm(overrides = {}) {
  const values = {
    Nome: 'Madeira Fina',
    Categoria: 'Marcenaria',
    'Telefone 1': '(11) 98765-4321',
    'E-mail 1': 'contato@madeirafina.com.br',
    Cidade: 'São Paulo',
    ...overrides,
  };

  Object.entries(values).forEach(([label, value]) => {
    fireEvent.change(screen.getByLabelText(label), { target: { value } });
  });
}

describe('CreateSupplierForm — obrigatoriedade em Fornecedores', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    addSupplierMock.mockResolvedValue({ id: 10 });
    updateSupplierMock.mockResolvedValue({ id: 10 });
  });

  it('salva fornecedor quando campos obrigatórios são preenchidos e opcionais ficam vazios', async () => {
    const onClose = vi.fn();
    render(<CreateSupplierForm onClose={onClose} />);

    fillSupplierForm();
    fireEvent.click(screen.getByRole('button', { name: 'Salvar fornecedor' }));

    await waitFor(() => expect(addSupplierMock).toHaveBeenCalledTimes(1));
    expect(addSupplierMock).toHaveBeenCalledWith(
      expect.objectContaining({
        name: 'Madeira Fina',
        category: 'Marcenaria',
        phone: '(11) 98765-4321',
        email: 'contato@madeirafina.com.br',
        city: 'São Paulo',
      }),
    );
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  const requiredCases = [
    ['Telefone 1', 'Telefone 1 é obrigatório.'],
    ['E-mail 1', 'E-mail 1 é obrigatório.'],
    ['Cidade', 'Cidade é obrigatória.'],
  ];

  it.each(requiredCases)('impede cadastro quando "%s" não é preenchido', async (fieldLabel, expectedError) => {
    render(<CreateSupplierForm onClose={vi.fn()} />);

    fillSupplierForm({ [fieldLabel]: '' });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar fornecedor' }));

    expect(await screen.findByRole('alert')).toHaveTextContent(expectedError);
    expect(addSupplierMock).not.toHaveBeenCalled();
  });

  it.each(requiredCases)('impede edição quando "%s" é apagado', async (fieldLabel, expectedError) => {
    const existing = {
      id: 7,
      name: 'Madeira Fina',
      category: 'Marcenaria',
      phone: '(11) 98765-4321',
      email: 'contato@madeirafina.com.br',
      city: 'São Paulo',
    };
    render(<CreateSupplierForm onClose={vi.fn()} supplier={existing} />);

    fireEvent.change(screen.getByLabelText(fieldLabel), { target: { value: '' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar alterações' }));

    expect(await screen.findByRole('alert')).toHaveTextContent(expectedError);
    expect(updateSupplierMock).not.toHaveBeenCalled();
  });
});
