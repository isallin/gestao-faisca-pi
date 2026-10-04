import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CalendarDays, Filter } from 'lucide-react';
import { useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button, FilterPanel, Segmented } from '@/shared/ui';
import { TaskCalendar } from './TaskCalendar';
import { TaskGantt } from './TaskGantt';
import { TaskList } from './TaskList';
import './TasksPage.css';

const MODES = [
  { value: 'monthly', label: 'Mensal' },
  { value: 'weekly', label: 'Semanal' },
];

export function TasksPage() {
  usePageTitle('Tarefas');
  const navigate = useNavigate();
  const { items: tasks } = useTasks();
  const [mode, setMode] = useState('monthly');
  const [filterOpen, setFilterOpen] = useState(false);
  const [status, setStatus] = useState('Todos');

  const filtered = useMemo(
    () => (status === 'Todos' ? tasks : tasks.filter((t) => t.status === status)),
    [tasks, status],
  );

  return (
    <>
      <header className="tasks-head">
        <h1>Tarefas</h1>
        <Segmented options={MODES} value={mode} onChange={setMode} />
      </header>

      <section className="tasks-layout">
        <aside className="task-list-column">
          <Button variant="secondary" onClick={() => navigate('/add-tarefa')}>
            + Adicionar Tarefa
          </Button>
          <TaskList tasks={filtered} />
        </aside>

        <div className="calendar-column">
          <header>
            <h2>
              <CalendarDays /> Agosto de 2026
            </h2>
            <Button variant="icon" onClick={() => setFilterOpen((v) => !v)} aria-label="Filtrar tarefas">
              <Filter />
            </Button>
          </header>

          {mode === 'monthly' ? <TaskCalendar tasks={filtered} /> : <TaskGantt tasks={filtered} />}

          <FilterPanel open={filterOpen}>
            <label>
              Status
              <select className="input" value={status} onChange={(e) => setStatus(e.target.value)}>
                <option>Todos</option>
                <option>Pendente</option>
                <option>Em andamento</option>
                <option>Concluído</option>
              </select>
            </label>
            <label>
              Projeto
              <select className="input">
                <option>Todos os projetos</option>
              </select>
            </label>
            <label>
              Prioridade
              <select className="input">
                <option>Todas</option>
              </select>
            </label>
          </FilterPanel>
        </div>
      </section>
    </>
  );
}
