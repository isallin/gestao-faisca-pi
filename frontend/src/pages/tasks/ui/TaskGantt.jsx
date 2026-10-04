import { taskTone } from '@/entities/task';

export function TaskGantt({ tasks }) {
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
            <span
              className={taskTone(task)}
              style={{ marginLeft: `${index * 5}%`, width: `${18 + (index % 3) * 7}%` }}
            >
              {task.project}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
