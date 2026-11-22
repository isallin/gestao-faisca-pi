import { useMemo } from 'react';
import { addDays, format, startOfMonth, startOfWeek } from 'date-fns';
import { taskEndsOnDate, taskTone } from '@/entities/task';

const WEEKDAYS = ['domingo', 'segunda-feira', 'terça-feira', 'quarta-feira', 'quinta-feira', 'sexta-feira', 'sábado'];
const MAX_PER_DAY = 3;

/** Calendário mensal com tarefas exibidas como barra colorida na data final. */
export function TaskCalendar({ tasks, month, onSelect }) {
  const monthStart = startOfMonth(month || new Date());
  const gridStart = startOfWeek(monthStart);
  const cells = useMemo(() => Array.from({ length: 42 }, (_, index) => addDays(gridStart, index)), [gridStart]);
  const today = format(new Date(), 'yyyy-MM-dd');

  return (
    <div className="calendar">
      <div className="weekdays">
        {WEEKDAYS.map((day) => (
          <strong key={day}>{day}</strong>
        ))}
      </div>
      <div className="month-grid">
        {cells.map((day) => {
          const outside = day.getMonth() !== monthStart.getMonth();
          const dayIso = format(day, 'yyyy-MM-dd');
          const dayTasks = outside ? [] : tasks.filter((task) => taskEndsOnDate(task, day));

          return (
            <div className={outside ? 'outside' : ''} key={dayIso}>
              <span className={dayIso === today ? 'today' : ''}>{format(day, 'd')}</span>
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
