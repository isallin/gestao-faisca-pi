/** Tarefa pendente com prioridade Alta ou Crítica. */
export const isUrgentTask = (task) => !task.done && ['Alta', 'Crítica'].includes(task.priority);

/** Classe de cor usada nas barras do calendário e do Gantt. */
export function taskTone(task) {
  if (task.priority === 'Alta' || task.priority === 'Crítica') return 'bar-danger';
  if (task.status === 'Concluído') return 'bar-success';
  if (task.status === 'Em andamento') return 'bar-info';
  return 'bar-primary';
}

const PRIORITY_WEIGHT = { Crítica: 0, Alta: 1, Média: 2, Baixa: 3 };
export const TASK_PRIORITIES = ['Baixa', 'Média', 'Alta', 'Crítica'];

/** Ordena por data de início e, em empate, por prioridade (Crítica primeiro). Não altera o array original. */
export function sortTasks(tasks) {
  return [...tasks].sort(
    (a, b) =>
      (a.start || '').localeCompare(b.start || '') ||
      (PRIORITY_WEIGHT[a.priority] ?? 99) - (PRIORITY_WEIGHT[b.priority] ?? 99),
  );
}

/** Total de horas registradas em uma tarefa. */
export const totalHours = (task) =>
  (task.timeLogs ?? []).reduce((sum, log) => sum + Number(log.hours || 0), 0);

/** "2026-08-03" → "03/08/2026" */
export const formatDate = (iso) => (iso ? iso.split('-').reverse().join('/') : '—');
