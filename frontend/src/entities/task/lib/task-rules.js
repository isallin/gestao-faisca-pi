/** Tarefa pendente com prioridade Alta ou Crítica. */
export const isUrgentTask = (task) => !task.done && ['Alta', 'Crítica'].includes(task.priority);

/** Classe de cor usada nas barras do calendário e do Gantt. */
export function taskTone(task) {
  if (task.priority === 'Alta' || task.priority === 'Crítica') return 'bar-danger';
  if (task.status === 'Concluído') return 'bar-success';
  if (task.status === 'Em andamento') return 'bar-info';
  return 'bar-primary';
}
