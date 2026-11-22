import { addDays, format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Gantt, Willow } from '@svar-ui/react-gantt';
import '@svar-ui/react-gantt/all.css';

const WEEK_SCALES = [
  { unit: 'week', step: 1, format: (date) => `Semana ${format(date, 'II', { locale: ptBR })}` },
  { unit: 'day', step: 1, format: (date) => format(date, 'dd/MM', { locale: ptBR }) },
];

const COLUMNS = [
  { id: 'text', header: 'Tarefa', width: 240 },
  { id: 'project', header: 'Projeto', width: 120 },
];

export function TaskGantt({ tasks = [], start, end, onSelect }) {
  const rangeStart = start || new Date();
  const rangeEnd = end || addDays(rangeStart, 13);

  return (
    <div className="gantt-scroll tasks-gantt">
      <Willow fonts={false}>
        <div className="gantt-lib-wrap">
          <Gantt
            tasks={tasks}
            scales={WEEK_SCALES}
            columns={COLUMNS}
            start={rangeStart}
            end={rangeEnd}
            cellWidth={64}
            cellHeight={38}
            autoScale={false}
            readonly
            onTaskClick={(event) => {
              const id = Number(event?.task?.id ?? event?.id ?? event?.data?.id);
              if (!Number.isNaN(id)) onSelect?.(id);
            }}
          />
        </div>
      </Willow>
    </div>
  );
}
