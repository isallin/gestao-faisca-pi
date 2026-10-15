import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CalendarDays, Filter } from 'lucide-react';
import { useProjects } from '@/entities/project';
import { formatDate, sortTasks, totalHours, useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button, FilterPanel, Segmented } from '@/shared/ui';
import { ALL, applyFilters, EntityList, initialFilterValues } from '@/widgets/entity-list';
import { TaskCalendar } from './TaskCalendar';
import { TaskDetailModal } from './TaskDetailModal';
import { TaskGantt } from './TaskGantt';
import { TaskList } from './TaskList';
import './TasksPage.css';

const MODES = [
  { value: 'monthly', label: 'Mensal' },
  { value: 'weekly', label: 'Semanal' },
  { value: 'list', label: 'Lista' },
];

const STATUSES = ['Pendente', 'Em andamento', 'Concluído'];
const PRIORITIES = ['Crítica', 'Alta', 'Média', 'Baixa'];

const optionsOf = (values, allLabel) => [
  { value: ALL, label: allLabel },
  ...values.map((v) => ({ value: v, label: v })),
];

const COLUMNS = [
  { key: 'name', label: 'Tarefa' },
  { key: 'project', label: 'Projeto' },
  { key: 'period', label: 'Prazo', render: (t) => `${formatDate(t.start)} a ${formatDate(t.end)}` },
];

const detail = (task) => {
  const done = (task.checklist ?? []).filter((i) => i.done).length;
  return [
    { label: 'Responsável', value: task.member },
    { label: 'Status', value: task.status },
    { label: 'Prioridade', value: task.priority },
    { label: 'Categoria', value: task.category },
    { label: 'Checklist', value: task.checklist?.length ? `${done} de ${task.checklist.length} concluídos` : '' },
    { label: 'Observações', value: task.note },
  ];
};

export function TasksPage() {
  usePageTitle('Tarefas');
  const navigate = useNavigate();
  const { items: tasks } = useTasks();
  const { items: projects } = useProjects();
  const [mode, setMode] = useState('monthly');
  const [filterOpen, setFilterOpen] = useState(false);
  const [selectedId, setSelectedId] = useState();

  const filters = useMemo(
    () => [
      {
        key: 'status',
        label: 'Status',
        options: optionsOf(STATUSES, 'Todos'),
        match: (task, value) => task.status === value,
      },
      {
        key: 'project',
        label: 'Projeto',
        options: optionsOf([...new Set(projects.map((p) => p.code))], 'Todos os projetos'),
        match: (task, value) => task.project === value,
      },
      {
        key: 'priority',
        label: 'Prioridade',
        options: optionsOf(PRIORITIES, 'Todas'),
        match: (task, value) => task.priority === value,
      },
    ],
    [projects],
  );
  const [values, setValues] = useState(() => initialFilterValues(filters));
  const changeFilter = (key, value) => setValues((v) => ({ ...v, [key]: value }));

  // Ordem das listas: data de início e, em seguida, prioridade.
  const sorted = useMemo(() => sortTasks(tasks), [tasks]);
  const filtered = useMemo(() => applyFilters(sorted, filters, values), [sorted, filters, values]);

  const modeSwitch = <Segmented options={MODES} value={mode} onChange={setMode} />;
  const modal = selectedId !== undefined && <TaskDetailModal taskId={selectedId} onClose={() => setSelectedId(undefined)} />;

  if (mode === 'list') {
    return (
      <>
        <EntityList
          title="Tarefas"
          addLabel="Adicionar Tarefa"
          rows={sorted}
          columns={COLUMNS}
          detail={detail}
          onAdd={() => navigate('/add-tarefa')}
          onEdit={(task) => navigate(`/editar-tarefa/${task.id}`)}
          filters={filters}
          filterValues={values}
          onFilterChange={changeFilter}
          headerExtra={modeSwitch}
          detailFooter={(task) => (
            <>
              <span>
                Tempo dedicado: <strong>{totalHours(task).toLocaleString('pt-BR')} h</strong>
              </span>
              <Button variant="secondary" onClick={() => setSelectedId(task.id)}>
                Registrar tempo
              </Button>
            </>
          )}
        />
        {modal}
      </>
    );
  }

  return (
    <>
      <header className="tasks-head">
        <h1>Tarefas</h1>
        <div className="tasks-button">
          <Button variant="secondary" onClick={() => navigate('/add-tarefa')}>
            + Adicionar Tarefa
          </Button>
          {modeSwitch}
        </div>
      </header>

      <section className="tasks-layout">
        <aside className="task-list-column">
          <TaskList tasks={filtered} onSelect={setSelectedId} />
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

          <FilterPanel open={filterOpen}>
            {filters.map((filter) => (
              <label key={filter.key}>
                {filter.label}
                <select
                  className="input"
                  value={values[filter.key]}
                  onChange={(e) => changeFilter(filter.key, e.target.value)}
                >
                  {filter.options.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>
            ))}
          </FilterPanel>
          {mode === 'monthly' ? (
            <TaskCalendar tasks={filtered} onSelect={setSelectedId} />
          ) : (
            <TaskGantt tasks={filtered} onSelect={setSelectedId} />
          )}
        </div>
      </section>
      {modal}
    </>
  );
}
