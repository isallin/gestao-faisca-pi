import { describe, expect, it } from 'vitest';
import { computeDashboard, monthlyBreakdown } from './dashboard-metrics';

// Quarta-feira, 21/10/2026 (semana: 19/10 a 25/10)
const TODAY = new Date(2026, 9, 21, 10);

const task = (id, end, extra = {}) => ({
  id,
  end,
  start: end,
  done: false,
  priority: 'Média',
  project: 'RES-024',
  ...extra,
});

describe('computeDashboard', () => {
  const tasks = [
    task(1, '2026-10-21', { priority: 'Baixa' }), // hoje, pendente
    task(2, '2026-10-21', { priority: 'Crítica' }), // hoje, pendente
    task(3, '2026-10-21', { done: true }), // hoje, concluída
    task(4, '2026-10-23'), // semana, pendente
    task(5, '2026-10-26'), // próxima semana
    task(6, '2026-10-19'), // atrasada (esta semana)
    task(7, '2026-08-10'), // atrasada
    task(8, '2026-08-12', {
      done: true,
      timeLogs: [
        { date: '2026-10-05', hours: 3 },
        { date: '2026-09-30', hours: 9 },
      ],
    }),
  ];
  const result = computeDashboard(tasks, TODAY);

  it('calcula pendentes de hoje e o percentual concluído', () => {
    expect(result.todayPending).toEqual({ value: 2, total: 3, percent: 33 });
  });

  it('calcula pendentes da semana sem contar atrasadas', () => {
    expect(result.weekPending.value).toBe(3); // tarefas 1, 2 e 4
  });

  it('calcula atrasadas sobre as tarefas em aberto', () => {
    expect(result.late).toEqual({ value: 2, total: 6, percent: 33 });
  });

  it('soma só as horas do mês atual', () => {
    expect(result.hours.value).toBe(3);
  });

  it('lista urgentes de hoje com a prioridade mais alta primeiro', () => {
    expect(result.urgentToday.map((t) => t.id)).toEqual([2, 1]);
  });

  it('não divide por zero sem tarefas', () => {
    const empty = computeDashboard([], TODAY);
    expect(empty.todayPending.percent).toBe(0);
    expect(empty.late.percent).toBe(0);
  });
});

describe('monthlyBreakdown', () => {
  it('agrupa os últimos 6 meses em concluídas, pendentes e atrasadas', () => {
    const months = monthlyBreakdown(
      [
        task(1, '2026-08-10', { done: true }),
        task(2, '2026-08-12'),
        task(3, '2026-10-30'),
        task(4, '2026-01-10'), // fora da janela
      ],
      TODAY,
    );
    expect(months.map((m) => m.month)).toEqual(['Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out']);
    expect(months[3]).toMatchObject({ done: 1, late: 1, pending: 0, total: 2 });
    expect(months[5]).toMatchObject({ pending: 1, total: 1 });
  });
});
