import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CreateProjectForm } from './CreateProjectForm';

const addMock = vi.fn();
const updateMock = vi.fn();
const reloadPhasesMock = vi.fn();

vi.mock('@/entities/client', () => ({
  useClients: () => ({
    items: [{ id: 1, name: 'Marina Almeida' }],
  }),
}));

vi.mock('@/entities/project', () => ({
  useProjects: () => ({
    add: addMock,
    update: updateMock,
    phases: [{ id: 1, nome: 'Executivo' }],
    phaseError: '',
    reloadPhases: reloadPhasesMock,
  }),
}));

function fillRequiredFields(overrides = {}) {
  const values = {
    Nome: 'Casa Paineiras',
    'Sigla/Código': 'RES-024',
    Cliente: '1',
    'Tipo de imóvel': 'Residencial',
    'Metragem (m²)': '180',
    CEP: '01310-100',
    Logradouro: 'Avenida Paulista',
    Número: '248',
    Complemento: 'Apto 12',
    Bairro: 'Bela Vista',
    Cidade: 'São Paulo',
    Estado: 'SP',
    País: 'Brasil',
    Fase: '1',
    'Data de início': '2026-11-01',
    'Data do fim': '2026-12-30',
    ...overrides,
  };

  Object.entries(values).forEach(([label, value]) => {
    fireEvent.change(screen.getByLabelText(label), { target: { value } });
  });
}

describe('CreateProjectForm — validação de obrigatoriedade', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    addMock.mockResolvedValue({ id: 99 });
    globalThis.fetch = vi.fn(() =>
      Promise.resolve({
        json: () =>
          Promise.resolve({
            logradouro: 'Avenida Paulista',
            bairro: 'Bela Vista',
            localidade: 'São Paulo',
            uf: 'SP',
          }),
      }),
    );
  });

  it('salva projeto quando todos os campos obrigatórios são preenchidos e Condomínio, Matrícula e Observação ficam vazios', async () => {
    const onClose = vi.fn();
    render(<CreateProjectForm onClose={onClose} />);

    fillRequiredFields();
    fireEvent.click(screen.getByRole('button', { name: 'Salvar projeto' }));

    await waitFor(() => expect(addMock).toHaveBeenCalledTimes(1));
    expect(addMock).toHaveBeenCalledWith(
      expect.objectContaining({
        nome: 'Casa Paineiras',
        codigo: 'RES-024',
        clienteId: 1,
        tipoImovel: 'Residencial',
        areaM2: 180,
        condominio: '',
        matricula: '',
        observacao: '',
        cep: '01310-100',
        logradouro: 'Avenida Paulista',
        numero: '248',
        complemento: 'Apto 12',
        bairro: 'Bela Vista',
        cidade: 'São Paulo',
        estado: 'SP',
        pais: 'Brasil',
        fkTarefaFase: 1,
        dataInicio: '2026-11-01T00:00:00',
        dataFim: '2026-12-30T00:00:00',
      }),
    );
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  const requiredCases = [
    ['Nome', 'Nome é obrigatório'],
    ['Sigla/Código', 'Código é obrigatório'],
    ['Cliente', 'Cliente é obrigatório'],
    ['Tipo de imóvel', 'Tipo de imóvel é obrigatório'],
    ['Metragem (m²)', 'Metragem é obrigatória'],
    ['CEP', 'CEP é obrigatório'],
    ['Logradouro', 'Logradouro é obrigatório'],
    ['Número', 'Número é obrigatório'],
    ['Complemento', 'Complemento é obrigatório'],
    ['Bairro', 'Bairro é obrigatório'],
    ['Cidade', 'Cidade é obrigatória'],
    ['Estado', 'Estado é obrigatório'],
    ['País', 'País é obrigatório'],
    ['Fase', 'Fase é obrigatória'],
    ['Data de início', 'Data de início é obrigatória'],
    ['Data do fim', 'Data do fim é obrigatória'],
  ];

  it.each(requiredCases)('exibe erro quando "%s" não é preenchido', async (fieldLabel, expectedError) => {
    render(<CreateProjectForm onClose={vi.fn()} />);

    fillRequiredFields({ [fieldLabel]: '' });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar projeto' }));

    expect(await screen.findByRole('alert')).toHaveTextContent(expectedError);
    expect(addMock).not.toHaveBeenCalled();
  });
});
