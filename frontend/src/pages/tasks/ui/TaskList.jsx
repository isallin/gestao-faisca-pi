import { useState } from 'react';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { CompleteTaskCheck } from '@/features/complete-task';
import { Button } from '@/shared/ui';

export function TaskList({ tasks }) {
  const [expandedId, setExpandedId] = useState();

  return (
    <div className="task-list">
      {tasks.map((task) => {
        const isOpen = expandedId === task.id;
        return (
          <article className={task.done ? 'task-item task-done' : 'task-item'} key={task.id}>
            <div>
              <small>{task.project}</small>
              <h3>{task.name}</h3>
              {isOpen && (
                <p>
                  {task.note}
                  <br />
                  <span>
                    {task.member} · {task.priority}
                  </span>
                </p>
              )}
              <Button variant="ghost" aria-expanded={isOpen} onClick={() => setExpandedId(isOpen ? undefined : task.id)}>
                {isOpen ? <ChevronUp /> : <ChevronDown />}
              </Button>
            </div>
            <CompleteTaskCheck task={task} />
            {task.done && <em>Concluído</em>}
          </article>
        );
      })}
    </div>
  );
}
