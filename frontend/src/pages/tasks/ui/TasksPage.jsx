import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { CalendarDays, ChevronLeft, ChevronRight, Filter } from 'lucide-react';
import { addMonths, endOfMonth, format, startOfMonth } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { DayPicker } from 'react-day-picker';
import { useProjects } from '@/entities/project';
import {
  buildGanttTasks,
  formatDate,
  sortTasks,
  toTimelineTask,
  totalHours,
  useTasks,
} from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button, FilterPanel, Segmented } from '@/shared/ui';
import {
  ALL,
  applyFilters,
  EntityList,
  FilterFields,
  initialFilterValues,
} from '@/widgets/entity-list';
import { GoogleCalendarChip } from './GoogleCalendarChip';
import { TaskCalendar } from './TaskCalendar';
import { TaskDetailModal } from './TaskDetailModal';
import { TaskGantt } from './TaskGantt';
import { TaskList } from './TaskList';
import './TasksPage.css';
import 'react-day-picker/dist/style.css';

const MODES = [
  { value: 'monthly', label: 'Mensal' },
  { value: 'weekly', label: 'Semanal' },
  { value: 'list', label: 'Lista' },
];

const STATUSES = ['Pendente', 'Em andamento', 'In Hold', 'Concluído', 'Cancelado'];
const initialMode = (param) => (param === 'weekly' || param === 'list' ? param : 'monthly');

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
    { label: 'Fase', value: task.stage },
    { label: 'Categoria', value: task.category },
    {
      label: 'Checklist',
      value: task.checklist?.length ? `${done} de ${task.checklist.length} concluídos` : '',
    },
    { label: 'Observações', value: task.note },
  ];
};

export function TasksPage() {
  usePageTitle('Tarefas');
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { items: tasks, isLoading, error, reload } = useTasks();
  const { items: projects } = useProjects();
  const [mode, setMode] = useState(() => initialMode(searchParams.get('view')));
  const [filterOpen, setFilterOpen] = useState(false);
  const [selected, setSelected] = useState();
  const [selectedMonth, setSelectedMonth] = useState(() => startOfMonth(new Date()));
  const [monthPickerOpen, setMonthPickerOpen] = useState(false);
  const monthPickerRef = useRef(null);
  const openDetails = (id) => setSelected({ id, register: false });

  useEffect(() => {
    setMode(initialMode(searchParams.get('view')));
  }, [searchParams]);

  useEffect(() => {
    if (!monthPickerOpen) return undefined;
    const closeOnOutside = (event) => {
      if (!monthPickerRef.current?.contains(event.target)) setMonthPickerOpen(false);
    };
    document.addEventListener('mousedown', closeOnOutside);
    return () => document.removeEventListener('mousedown', closeOnOutside);
  }, [monthPickerOpen]);

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
        options: optionsOf([...new Set(tasks.map((task) => task.priority).filter(Boolean))], 'Todas'),
        match: (task, value) => task.priority === value,
      },
    ],
    [projects, tasks],
  );
  const [values, setValues] = useState(() => initialFilterValues(filters));
  const changeFilter = (key, value) => setValues((v) => ({ ...v, [key]: value }));

  useEffect(() => {
    setValues((current) => {
      const next = { ...current };
      filters.forEach((filter) => {
        if (next[filter.key] === undefined) next[filter.key] = filter.initialValue ?? ALL;
      });
      return next;
    });
  }, [filters]);

  // Ordem das listas: data de início e, em seguida, prioridade.
  const sorted = useMemo(() => sortTasks(tasks), [tasks]);
  const filtered = useMemo(() => applyFilters(sorted, filters, values), [sorted, filters, values]);
  const monthFiltered = useMemo(
    () =>
      filtered.filter((task) => {
        const endDate = toTimelineTask(task).endDate;
        return endDate && endDate.getMonth() === selectedMonth.getMonth() && endDate.getFullYear() === selectedMonth.getFullYear();
      }),
    [filtered, selectedMonth],
  );
  const ganttTasks = useMemo(
    () =>
      buildGanttTasks(
        monthFiltered.filter((task) => task.status !== 'Concluído' && task.status !== 'Concluido'),
      ),
    [monthFiltered],
  );
  const monthLabel = useMemo(() => {
    const value = format(selectedMonth, "LLLL 'de' yyyy", { locale: ptBR });
    return value.charAt(0).toUpperCase() + value.slice(1);
  }, [selectedMonth]);

  const monthStart = startOfMonth(selectedMonth);
  const monthEnd = endOfMonth(selectedMonth);
  const updateSelectedMonth = (date) => setSelectedMonth(startOfMonth(date));
  const currentYear = new Date().getFullYear();
  const pickerStartMonth = new Date(currentYear - 5, 0, 1);
  const pickerEndMonth = new Date(currentYear + 5, 11, 1);

  const modeSwitch = <Segmented options={MODES} value={mode} onChange={setMode} />;
  const calendarChip = <GoogleCalendarChip />;
  const modal = selected && (
    <TaskDetailModal
      key={`${selected.id}-${selected.register}`}
      taskId={selected.id}
      startRegistering={selected.register}
      onClose={() => setSelected(undefined)}
    />
  );

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
          onDelete={(task) => navigate(`/excluir-tarefa/${task.id}`)}
          filters={filters}
          filterValues={values}
          onFilterChange={changeFilter}
          headerExtra={
            <>
              {calendarChip}
              {modeSwitch}
            </>
          }
          detailFooter={(task) => (
            <>
              <span>
                Tempo dedicado: <strong>{totalHours(task).toLocaleString('pt-BR')} h</strong>
              </span>
              <Button
                variant="secondary"
                onClick={() => setSelected({ id: task.id, register: true })}
              >
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
        <div className="tasks-title">
          <h1>Tarefas</h1>
          {calendarChip}
        </div>
        <div className="tasks-button">
          <Button variant="ghost" onClick={reload}>
            Recarregar
          </Button>
          <Button variant="secondary" onClick={() => navigate('/add-tarefa')}>
            + Adicionar Tarefa
          </Button>
          {modeSwitch}
        </div>
      </header>

      <section className={mode === 'weekly' ? 'tasks-layout tasks-layout-weekly' : 'tasks-layout'}>
        <header className="tasks-visual-head">
          <h2 className="month-title" ref={monthPickerRef}>
              <Button
                variant="icon"
                aria-label="Mês anterior"
                onClick={() => setSelectedMonth((value) => startOfMonth(addMonths(value, -1)))}
              >
                <ChevronLeft />
              </Button>
            <button
              type="button"
              className="month-picker-trigger"
              onClick={() => setMonthPickerOpen((value) => !value)}
              aria-label="Selecionar mês"
            >
              <CalendarDays /> {monthLabel}
            </button>
              <Button
                variant="icon"
                aria-label="Próximo mês"
                onClick={() => setSelectedMonth((value) => startOfMonth(addMonths(value, 1)))}
              >
                <ChevronRight />
              </Button>
            {monthPickerOpen && (
              <div className="month-picker-popover" role="dialog" aria-modal="false">
                <DayPicker
                  mode="single"
                  locale={ptBR}
                  month={selectedMonth}
                  selected={selectedMonth}
                  startMonth={pickerStartMonth}
                  endMonth={pickerEndMonth}
                  captionLayout="dropdown"
                  onMonthChange={updateSelectedMonth}
                  onSelect={(date) => {
                    if (!date) return;
                    updateSelectedMonth(date);
                    setMonthPickerOpen(false);
                  }}
                />
              </div>
            )}
          </h2>

          <Button
            variant="icon"
            onClick={() => setFilterOpen((v) => !v)}
            aria-label="Filtrar tarefas"
          >
            <Filter />
          </Button>
        </header>

        <FilterPanel open={filterOpen} className="tasks-visual-filter">
          <FilterFields filters={filters} values={values} onChange={changeFilter} />
        </FilterPanel>

        {mode !== 'weekly' && (
          <aside className="task-list-column">
            <TaskList tasks={filtered} onSelect={openDetails} />
          </aside>
        )}

        <div className="calendar-column">
          {!isLoading && error && <p className="task-empty">{error}</p>}
          {isLoading && <p className="task-empty">Carregando tarefas...</p>}
          {mode === 'monthly' ? (
            <TaskCalendar tasks={monthFiltered} month={selectedMonth} onSelect={openDetails} />
          ) : (
            <TaskGantt tasks={ganttTasks} start={monthStart} end={monthEnd} onSelect={openDetails} />
          )}
        </div>
      </section>
      {modal}
    </>
  );
}
