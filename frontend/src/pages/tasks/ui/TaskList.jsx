import { useState } from 'react';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { formatDate } from '@/entities/task';
import { CompleteTaskCheck } from '@/features/complete-task';
import { Button } from '@/shared/ui';

export function TaskList({ tasks, onSelect }) {
  const [expandedId, setExpandedId] = useState();

  return (
    <div className="task-list">
      {tasks.map((task) => {
        const isOpen = expandedId === task.id;
        return (
          <article className={task.done ? 'task-item task-done' : 'task-item'} key={task.id}>
            <div>
              <small>
                {task.project} · {formatDate(task.start)}
              </small>
              <h3>{task.name}</h3>
              {isOpen && (
                <p>
                  {task.note}
                  <br />
                  <span>
                    {task.member} · Prioridade: {task.priority}
                  </span>
                </p>
              )}
              <div className="task-item-actions">
                <Button variant="ghost" aria-expanded={isOpen} aria-label={isOpen ? 'Recolher' : 'Expandir'} onClick={() => setExpandedId(isOpen ? undefined : task.id)}>
                  {isOpen ? <ChevronUp /> : <ChevronDown />}
                </Button>
                {isOpen && (
                  <Button variant="secondary" onClick={() => onSelect?.(task.id)}>
                    Ver detalhes
                  </Button>
                )}
              </div>
            </div>
            <CompleteTaskCheck task={task} />
            {task.done && <em>Concluído</em>}
          </article>
        );
      })}
      {tasks.length === 0 && <p className="task-empty">Nenhuma tarefa encontrada.</p>}
    </div>
  );
}
