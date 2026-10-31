import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button } from '@/shared/ui';
import { ALL, EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Projeto' },
  { key: 'code', label: 'Código do Projeto' },
  { key: 'status', label: 'Status' },
];

const STATUSES = ['Planejamento', 'Em andamento', 'Aguardando', 'Concluído'];

const FILTERS = [
  {
    key: 'status',
    label: 'Status',
    options: [{ value: ALL, label: 'Todos' }, ...STATUSES.map((status) => ({ value: status, label: status }))],
    match: (project, value) => project.status === value,
  },
];

const detail = (project, clientsById) => [
  { label: 'Cliente', value: project.client ?? clientsById.get(project.clientId) ?? 'Não informado' },
  { label: 'Etapa', value: project.stage },
  { label: 'Local', value: project.address },
  { label: 'Observação', value: project.note },
];

export function ProjectsPage() {
  usePageTitle('Projetos');
  const navigate = useNavigate();
  const { items, reload } = useProjects();
  const { items: clients } = useClients();
  const clientsById = useMemo(
    () => new Map(clients.map((client) => [client.id, client.name])),
    [clients],
  );

  return (
    <EntityList
      title="Projetos"
      addLabel="Adicionar Projeto"
      rows={items}
      columns={COLUMNS}
      detail={(project) => detail(project, clientsById)}
      onAdd={() => navigate('/add-projeto')}
      onEdit={(project) => navigate(`/editar-projeto/${project.id}`)}
      filters={FILTERS}
      headerExtra={
        <Button variant="ghost" onClick={reload}>
          Recarregar
        </Button>
      }
    />
  );
}
