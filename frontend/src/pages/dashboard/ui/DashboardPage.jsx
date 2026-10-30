import { useMemo, useState } from 'react';
import {
  AlertTriangle,
  CalendarCheck2,
  CalendarClock,
  CalendarDays,
  CircleCheckBig,
  Clock3,
} from 'lucide-react';
import { useProjects } from '@/entities/project';
import { useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { SelectField } from '@/shared/ui';
import { computeDashboard } from '../lib/dashboard-metrics';
import { KpiCard } from './KpiCard';
import './DashboardPage.css';

const ALL_PROJECTS = 'all';

const SERIES = [
  { key: 'done', label: 'Concluídas' },
  { key: 'pending', label: 'Pendentes' },
  { key: 'late', label: 'Atrasadas' },
];

export function DashboardPage() {
  usePageTitle('Dashboard');
  const { items: projects } = useProjects();
  const { items: tasks } = useTasks();
  const [projectCode, setProjectCode] = useState(ALL_PROJECTS);

  const isFiltered = projectCode !== ALL_PROJECTS;
  const projectTasks = useMemo(
    () => (isFiltered ? tasks.filter((t) => t.project === projectCode) : tasks),
    [tasks, projectCode, isFiltered],
  );

  const { todayPending, weekPending, late, hours, urgentToday, months } = useMemo(
    () => computeDashboard(projectTasks),
    [projectTasks],
  );
  const maxMonth = Math.max(1, ...months.map((m) => m.total));
  const hasChartData = months.some((m) => m.total > 0);

  return (
    <div className="dashboard">
      <header className="dashboard-head">
        <div>
          <small>Visão geral do escritório</small>
          <h1>Dashboard</h1>
        </div>
        <div className="dashboard-filter">
          <SelectField
            label="Projeto"
            value={projectCode}
            onChange={(e) => setProjectCode(e.target.value)}
          >
            <option value={ALL_PROJECTS}>Todos os projetos</option>
            {projects.map((project) => (
              <option key={project.id} value={project.code}>
                {project.code} · {project.name}
              </option>
            ))}
          </SelectField>
        </div>
      </header>

      <section className="kpi-grid">
        <KpiCard
          icon={<CalendarClock />}
          label="Tarefas pendentes (hoje)"
          value={todayPending.value}
          caption={`de ${todayPending.total} previstas hoje`}
          percentHint="Percentual das tarefas de hoje já concluídas"
          tone="amber"
          percent={todayPending.percent}
        />
        <KpiCard
          icon={<CalendarDays />}
          label="Tarefas pendentes (semana)"
          value={weekPending.value}
          caption={`de ${weekPending.total} previstas na semana`}
          percentHint="Percentual das tarefas da semana já concluídas"
          tone="teal"
          percent={weekPending.percent}
        />
        <KpiCard
          icon={<AlertTriangle />}
          label="Tarefas atrasadas"
          value={late.value}
          caption={`de ${late.total} em aberto`}
          percentHint="Percentual das tarefas em aberto que estão atrasadas"
          tone="red"
          percent={late.percent}
        />
        <KpiCard
          icon={<Clock3 />}
          label="Horas trabalhadas no mês"
          value={`${hours.value}h`}
          caption={`meta de ${hours.goal}h`}
          percentHint="Percentual da meta mensal de horas"
          tone="green"
          percent={hours.percent}
        />
      </section>

      <section className="dash-grid">
        <div className="dash-panel urgent">
          <header>
            <h2>
              <CalendarCheck2 size={20} /> Tarefas urgentes
            </h2>
            <span>{urgentToday.length} para hoje</span>
          </header>
          {urgentToday.length === 0 && (
            <div className="dash-empty">
              <CircleCheckBig size={32} />
              <strong>Tudo em dia por hoje</strong>
              <small>Nenhuma tarefa pendente para hoje.</small>
            </div>
          )}
          {urgentToday.map((task) => (
            <div className="urgent-row" key={task.id}>
              <i className={task.priority === 'Crítica' ? 'critical' : ''} />
              <div>
                <strong>{task.name}</strong>
                <small>
                  {task.project} · {task.member}
                </small>
              </div>
              <span className="urgent-priority">{task.priority}</span>
            </div>
          ))}
        </div>

        <div className="dash-panel chart">
          <header>
            <h2>Tarefas por mês</h2>
            <small>Últimos 6 meses</small>
          </header>
          <ul className="chart-legend">
            {SERIES.map(({ key, label }) => (
              <li key={key}>
                <i className={`seg-${key}`} /> {label}
              </li>
            ))}
          </ul>
          <div
            className="bars"
            role="img"
            aria-label="Tarefas concluídas, pendentes e atrasadas por mês"
          >
            {months.map((m) => (
              <div className="bar-col" key={m.key}>
                <div className="bar-track">
                  {m.total > 0 && (
                    <div className="stack" style={{ height: `${(m.total / maxMonth) * 100}%` }}>
                      {SERIES.filter(({ key }) => m[key] > 0).map(({ key, label }) => (
                        <span
                          key={key}
                          className={`seg-${key}`}
                          style={{ flexGrow: m[key] }}
                          title={`${label}: ${m[key]}`}
                        >
                          {m[key]}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
                <small>{m.month}</small>
              </div>
            ))}
          </div>
          {!hasChartData && <p className="chart-empty">Sem tarefas nos últimos 6 meses.</p>}
        </div>
      </section>
    </div>
  );
}
