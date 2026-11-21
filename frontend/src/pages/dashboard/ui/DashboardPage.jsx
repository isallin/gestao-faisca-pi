import { useEffect, useState } from 'react';
import {
  AlertTriangle,
  CalendarCheck2,
  CalendarClock,
  CalendarDays,
  CircleCheckBig,
  Clock3,
} from 'lucide-react';
import { useProjects } from '@/entities/project';
import { getDashboard } from '@/shared/api/dashboard';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { SelectField } from '@/shared/ui';
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
  const [projectCode, setProjectCode] = useState(ALL_PROJECTS);
  const [dashboard, setDashboard] = useState(() => ({
    todayPending: { value: 0, total: 0, percent: 0 },
    weekPending: { value: 0, total: 0, percent: 0 },
    late: { value: 0, total: 0, percent: 0 },
    hours: { value: 0, goal: 160, percent: 0 },
    urgentToday: [],
    months: [],
  }));

  useEffect(() => {
    let active = true;

    const loadDashboard = async () => {
      try {
        const response = await getDashboard(projectCode);
        if (!active) return;
        setDashboard({
          todayPending: response?.todayPending ?? { value: 0, total: 0, percent: 0 },
          weekPending: response?.weekPending ?? { value: 0, total: 0, percent: 0 },
          late: response?.late ?? { value: 0, total: 0, percent: 0 },
          hours: response?.hours ?? { value: 0, goal: 160, percent: 0 },
          urgentToday: response?.urgentToday ?? [],
          months: response?.months ?? [],
        });
      } catch {
        if (!active) return;
        setDashboard({
          todayPending: { value: 0, total: 0, percent: 0 },
          weekPending: { value: 0, total: 0, percent: 0 },
          late: { value: 0, total: 0, percent: 0 },
          hours: { value: 0, goal: 160, percent: 0 },
          urgentToday: [],
          months: [],
        });
      }
    };

    loadDashboard();

    return () => {
      active = false;
    };
  }, [projectCode]);

  const { todayPending, weekPending, late, hours, urgentToday, months } = dashboard;
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
          caption={`de ${todayPending.total} tarefa(s) para hoje`}
          percentHint="Percentual de tarefas pendentes de hoje"
          tone="amber"
          percent={todayPending.percent}
        />
        <KpiCard
          icon={<CalendarDays />}
          label="Tarefas pendentes (semana)"
          value={weekPending.value}
          caption={`de ${weekPending.total} tarefa(s) para essa semana`}
          percentHint="Percentual de tarefas pendentes da semana"
          tone="teal"
          percent={weekPending.percent}
        />
        <KpiCard
          icon={<AlertTriangle />}
          label="Tarefas atrasadas"
          value={late.value}
          caption={`de ${late.total} tarefa(s) pendentes`}
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
