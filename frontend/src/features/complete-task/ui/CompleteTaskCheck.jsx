import { useTasks } from '@/entities/task';
import { CheckRail } from '@/shared/ui';

/** Marca/desmarca a tarefa como concluída e sincroniza o status. */
export function CompleteTaskCheck({ task }) {
  const { update } = useTasks();
  const toggle = () =>
    update(task.id, (t) => ({ done: !t.done, status: !t.done ? 'Concluído' : 'Em andamento' }));

  return <CheckRail checked={task.done} onChange={toggle} label={`Concluir ${task.name}`} />;
}
