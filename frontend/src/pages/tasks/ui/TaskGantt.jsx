import { taskTone } from '@/entities/task';

export function TaskGantt({ tasks, onSelect }) {
  return (
    <div className="gantt-scroll">
      <div className="gantt">
        <div className="gantt-days">
          {Array.from({ length: 14 }, (_, i) => (
            <span key={i}>{i + 1}/08</span>
          ))}
        </div>
        {tasks.map((task, index) => (
          <div className="gantt-row" key={task.id}>
            <strong>{task.name}</strong>
            <button
              type="button"
              className={`${taskTone(task)} gantt-bar`}
              style={{ marginLeft: `${index * 5}%`, width: `${18 + (index % 3) * 7}%` }}
              onClick={() => onSelect?.(task.id)}
            >
              {task.project}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
