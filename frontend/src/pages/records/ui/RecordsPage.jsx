import { useMemo } from 'react';
import { useProjects } from '@/entities/project';
import { formatDate, useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { ALL, EntityList } from '@/widgets/entity-list';
import { collectRecords, formatDateTime, formatHours } from '../lib/records';

const COLUMNS = [
  { key: 'task', label: 'Tarefa' },
  { key: 'project', label: 'Projeto' },
  { key: 'date', label: 'Data', render: (record) => formatDate(record.date) },
  { key: 'hours', label: 'Tempo', render: (record) => formatHours(record.hours) },
];

const detail = (record) => [
  { label: 'Responsável', value: record.member },
  { label: 'Início', value: formatDateTime(record.start) },
  { label: 'Fim', value: formatDateTime(record.end) },
  { label: 'Descrição', value: record.note },
];

export function RecordsPage() {
  usePageTitle('Registros');
  const { items: tasks } = useTasks();
  const { items: projects } = useProjects();

  const records = useMemo(() => collectRecords(tasks), [tasks]);
  const filters = useMemo(
    () => [
      {
        key: 'project',
        label: 'Projeto',
        options: [
          { value: ALL, label: 'Todos os projetos' },
          ...[...new Set(projects.map((p) => p.code))].map((code) => ({
            value: code,
            label: code,
          })),
        ],
        match: (record, value) => record.project === value,
      },
    ],
    [projects],
  );

  return (
    <EntityList
      title="Registros"
      rows={records}
      columns={COLUMNS}
      detail={detail}
      filters={filters}
      emptyMessage="Nenhum registro de tempo encontrado."
    />
  );
}
