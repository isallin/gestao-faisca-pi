import { totalHours } from '@/entities/task';

/** Meta mensal de horas usada no percentual do KPI "Horas trabalhadas no mês". */
export const MONTHLY_HOURS_GOAL = 160;

const MONTH_LABELS = [
  'Jan',
  'Fev',
  'Mar',
  'Abr',
  'Mai',
  'Jun',
  'Jul',
  'Ago',
  'Set',
  'Out',
  'Nov',
  'Dez',
];
const PRIORITY_WEIGHT = { Crítica: 0, Alta: 1, Média: 2, Baixa: 3 };

const pad = (n) => String(n).padStart(2, '0');

/** Date → "AAAA-MM-DD" no fuso local (evita o deslocamento de toISOString). */
export const toISODate = (date) =>
  `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;

const percentOf = (part, total) =>
  total > 0 ? Math.min(100, Math.round((part / total) * 100)) : 0;

/** Segunda a domingo da semana de `date`. */
function weekRange(date) {
  const start = new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDate() - ((date.getDay() + 6) % 7),
  );
  const end = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 6);
  return { start: toISODate(start), end: toISODate(end) };
}

/** Situação de uma tarefa em relação a hoje: 'done' | 'late' | 'pending'. */
export function taskState(task, todayISO) {
  if (task.done) return 'done';
  return task.end < todayISO ? 'late' : 'pending';
}

/**
 * Os 6 últimos meses (terminando no mês atual) com a contagem de tarefas
 * concluídas, pendentes e atrasadas, agrupadas pelo mês do prazo (`end`).
 */
export function monthlyBreakdown(tasks, today = new Date(), months = 6) {
  const todayISO = toISODate(today);
  const buckets = Array.from({ length: months }, (_, i) => {
    const d = new Date(today.getFullYear(), today.getMonth() - (months - 1 - i), 1);
    return {
      key: `${d.getFullYear()}-${pad(d.getMonth() + 1)}`,
      month: MONTH_LABELS[d.getMonth()],
      done: 0,
      pending: 0,
      late: 0,
    };
  });
  const byKey = new Map(buckets.map((b) => [b.key, b]));

  tasks.forEach((task) => {
    const bucket = byKey.get((task.end || '').slice(0, 7));
    if (bucket) bucket[taskState(task, todayISO)] += 1;
  });

  return buckets.map((b) => ({ ...b, total: b.done + b.pending + b.late }));
}

/** KPIs e listas do dashboard para um conjunto de tarefas já filtrado por projeto. */
export function computeDashboard(tasks, today = new Date()) {
  const todayISO = toISODate(today);
  const week = weekRange(today);
  const monthKey = todayISO.slice(0, 7);

  const dueToday = tasks.filter((t) => t.end === todayISO);
  const dueTodayPending = dueToday.filter((t) => !t.done);

  const dueWeek = tasks.filter((t) => t.end >= week.start && t.end <= week.end);
  const dueWeekPending = dueWeek.filter((t) => !t.done && t.end >= todayISO);

  const pending = tasks.filter((t) => !t.done);
  const late = pending.filter((t) => t.end < todayISO);

  const monthHours = tasks.reduce(
    (sum, task) =>
      sum +
      totalHours({
        timeLogs: (task.timeLogs ?? []).filter((log) => (log.date || '').startsWith(monthKey)),
      }),
    0,
  );

  return {
    todayPending: {
      value: dueTodayPending.length,
      total: dueToday.length,
      percent: percentOf(dueToday.length - dueTodayPending.length, dueToday.length),
    },
    weekPending: {
      value: dueWeekPending.length,
      total: dueWeek.length,
      percent: percentOf(dueWeek.length - dueWeekPending.length, dueWeek.length),
    },
    late: {
      value: late.length,
      total: pending.length,
      percent: percentOf(late.length, pending.length),
    },
    hours: {
      value: Math.round(monthHours * 10) / 10,
      goal: MONTHLY_HOURS_GOAL,
      percent: percentOf(monthHours, MONTHLY_HOURS_GOAL),
    },
    urgentToday: [...dueTodayPending].sort(
      (a, b) => (PRIORITY_WEIGHT[a.priority] ?? 99) - (PRIORITY_WEIGHT[b.priority] ?? 99),
    ),
    months: monthlyBreakdown(tasks, today),
  };
}
