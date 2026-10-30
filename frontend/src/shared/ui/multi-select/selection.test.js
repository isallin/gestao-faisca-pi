import { describe, expect, it } from 'vitest';
import { ALL, summarize, toggleAll, toggleOption } from './selection';
import { applyFilters } from '@/widgets/entity-list/lib/filters';

const all = ['Crítica', 'Alta', 'Média', 'Baixa'];
const items = all.map((value) => ({ value, label: value }));

describe('seleção do filtro múltiplo', () => {
  it('desmarcar uma opção com tudo marcado mantém as demais', () => {
    expect(toggleOption(ALL, all, 'Baixa')).toEqual(['Crítica', 'Alta', 'Média']);
  });

  it('marcar a última opção volta para "tudo" (sem filtro)', () => {
    expect(toggleOption(['Crítica', 'Alta', 'Média'], all, 'Baixa')).toBe(ALL);
  });

  it('mantém a ordem das opções ao marcar', () => {
    expect(toggleOption(['Baixa'], all, 'Crítica')).toEqual(['Crítica', 'Baixa']);
  });

  it('"Selecionar tudo" alterna entre nada e tudo', () => {
    expect(toggleAll(ALL)).toEqual([]);
    expect(toggleAll([])).toBe(ALL);
    expect(toggleAll(['Alta'])).toBe(ALL);
  });

  it('resume a seleção no botão', () => {
    expect(summarize(ALL, items, 'Todas')).toBe('Todas');
    expect(summarize([], items, 'Todas')).toBe('Nenhum');
    expect(summarize(['Alta'], items, 'Todas')).toBe('Alta');
    expect(summarize(['Crítica', 'Alta'], items, 'Todas')).toBe('Crítica, Alta');
    expect(summarize(['Crítica', 'Alta', 'Média'], items, 'Todas')).toBe('3 selecionados');
  });
});

describe('applyFilters com múltiplos valores', () => {
  const rows = [
    { id: 1, priority: 'Alta', project: 'A' },
    { id: 2, priority: 'Crítica', project: 'B' },
    { id: 3, priority: 'Baixa', project: 'A' },
  ];
  const filters = [
    { key: 'priority', match: (row, v) => row.priority === v },
    { key: 'project', match: (row, v) => row.project === v },
  ];

  it('une os valores do mesmo filtro (OU) e cruza filtros diferentes (E)', () => {
    expect(
      applyFilters(rows, filters, { priority: ['Alta', 'Crítica'], project: ALL }).map((r) => r.id),
    ).toEqual([1, 2]);
    expect(
      applyFilters(rows, filters, { priority: ['Alta', 'Crítica'], project: ['A'] }).map(
        (r) => r.id,
      ),
    ).toEqual([1]);
  });

  it('lista vazia não mostra nada e ALL não filtra', () => {
    expect(applyFilters(rows, filters, { priority: [] })).toEqual([]);
    expect(applyFilters(rows, filters, { priority: ALL })).toHaveLength(3);
  });

  it('continua aceitando um único valor em texto', () => {
    expect(applyFilters(rows, filters, { priority: 'Baixa' }).map((r) => r.id)).toEqual([3]);
  });
});
