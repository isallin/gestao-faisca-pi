import { AlertTriangle, Clock3, Filter, FolderKanban, ListChecks } from 'lucide-react';
import { useProjects } from '@/entities/project';
import { isUrgentTask, useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button } from '@/shared/ui';
import { KpiCard } from './KpiCard';
import './DashboardPage.css';

const DONE_BY_MONTH = [
  { month: 'Mar', height: 42 },
  { month: 'Abr', height: 58 },
  { month: 'Mai', height: 48 },
  { month: 'Jun', height: 76 },
  { month: 'Jul', height: 66 },
  { month: 'Ago', height: 88 },
];

const formatDay = (isoDate) =>
  new Date(`${isoDate}T12:00`).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });

export function DashboardPage() {
  usePageTitle('Dashboard');
  const { items: projects } = useProjects();
  const { items: tasks } = useTasks();

  const activeProjects = projects.filter((p) => p.status === 'Em andamento').length;
  const pendingTasks = tasks.filter((t) => !t.done).length;
  const urgentTasks = tasks.filter(isUrgentTask);
  const monthHours = tasks.length * 7;

  return (
    <>
      <header className="dashboard-head">
        <div>
          <small>Visão geral do escritório</small>
          <h1>Dashboard</h1>
        </div>
        <Button variant="icon" aria-label="Filtrar indicadores">
          <Filter />
        </Button>
      </header>

      <section className="kpi-grid">
        <KpiCard icon={<FolderKanban />} label="Projetos ativos" value={activeProjects} tone="amber" percent={72} />
        <KpiCard icon={<ListChecks />} label="Tarefas pendentes" value={pendingTasks} tone="red" percent={58} />
        <KpiCard icon={<Clock3 />} label="Horas no mês" value={monthHours} tone="green" percent={81} />
      </section>

      <section className="dash-grid">
        <div className="dash-panel urgent">
          <header>
            <h2>
              <AlertTriangle size={20} /> Tarefas urgentes
            </h2>
            <span>{urgentTasks.length} pendentes</span>
          </header>
          {urgentTasks.map((task) => (
            <div className="urgent-row" key={task.id}>
              <i className={task.priority === 'Crítica' ? 'critical' : ''} />
              <div>
                <strong>{task.name}</strong>
                <small>
                  {task.project} · {task.member}
                </small>
              </div>
              <time>{formatDay(task.end)}</time>
            </div>
          ))}
        </div>

        <div className="dash-panel chart">
          <header>
            <h2>Tarefas concluídas</h2>
            <small>Últimos 6 meses</small>
          </header>
          <div className="bars">
            {DONE_BY_MONTH.map(({ month, height }) => (
              <div key={month}>
                <span style={{ height: `${height}%` }} />
                <small>{month}</small>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="dash-panel recent">
        <header>
          <h2>Projetos recentes</h2>
          <span>Etapa atual</span>
        </header>
        {projects.slice(0, 4).map((project) => (
          <div key={project.id}>
            <strong>{project.name}</strong>
            <span>{project.code}</span>
            <em className={project.status === 'Concluído' ? 'status done' : 'status'}>{project.status}</em>
            <span>{project.stage}</span>
          </div>
        ))}
      </section>
    </>
  );
}
