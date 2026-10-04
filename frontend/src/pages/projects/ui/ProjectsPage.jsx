import { useNavigate } from 'react-router-dom';
import { useProjects } from '@/entities/project';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Projeto' },
  { key: 'code', label: 'Código do Projeto' },
  { key: 'status', label: 'Status' },
  { key: 'client', label: 'Cliente' },
];

const detail = (project) => [
  { label: 'Etapa', value: project.stage },
  { label: 'Local', value: project.address },
  { label: 'Observação', value: project.note },
];

export function ProjectsPage() {
  usePageTitle('Projetos');
  const navigate = useNavigate();
  const { items } = useProjects();

  return (
    <EntityList
      title="Projetos"
      addLabel="Adicionar Projeto"
      rows={items}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-projeto')}
    />
  );
}
