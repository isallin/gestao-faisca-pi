import { taskTone } from '@/entities/task';

const WEEKDAYS = ['domingo', 'segunda-feira', 'terça-feira', 'quarta-feira', 'quinta-feira', 'sexta-feira', 'sábado'];
// Agosto/2026 começa em um sábado: o 1º dia cai na 7ª célula da grade.
const CELLS = Array.from({ length: 42 }, (_, i) => i - 5);
const TODAY = 3;
const MAX_PER_DAY = 3;

/** Cada tarefa aparece no dia de início, identificada pelo código do projeto. */
export function TaskCalendar({ tasks, onSelect }) {
  return (
    <div className="calendar">
      <div className="weekdays">
        {WEEKDAYS.map((day) => (
          <strong key={day}>{day}</strong>
        ))}
      </div>
      <div className="month-grid">
        {CELLS.map((day, index) => {
          const outside = day < 1 || day > 31;
          const label = day < 1 ? 31 + day : day > 31 ? day - 31 : day;
          const dayTasks = outside ? [] : tasks.filter((task) => Number(task.start.slice(-2)) === day);
          return (
            <div className={outside ? 'outside' : ''} key={index}>
              <span className={day === TODAY ? 'today' : ''}>{label}</span>
              {dayTasks.slice(0, MAX_PER_DAY).map((task) => (
                <button
                  key={task.id}
                  type="button"
                  className={`${taskTone(task)} task-chip`}
                  title={`${task.name}: ${task.start} a ${task.end}`}
                  aria-label={`${task.project} — ${task.name}`}
                  onClick={() => onSelect?.(task.id)}
                >
                  {task.project}
                </button>
              ))}
              {dayTasks.length > MAX_PER_DAY && <small>+{dayTasks.length - MAX_PER_DAY}</small>}
            </div>
          );
        })}
      </div>
    </div>
  );
}
