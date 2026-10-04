import { useNavigate } from 'react-router-dom';
import { useCollaborators } from '@/entities/collaborator';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Colaborador' },
  { key: 'role', label: 'Cargo' },
  { key: 'projects', label: 'Projetos envolvidos' },
];

const detail = (person) => [
  { label: 'E-mail', value: person.email },
  { label: 'Projetos envolvidos', value: person.projects },
];

export function CollaboratorsPage() {
  usePageTitle('Colaboradores');
  const navigate = useNavigate();
  const { items } = useCollaborators();

  return (
    <EntityList
      title="Colaboradores"
      addLabel="Adicionar Colaborador"
      rows={items}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-colaborador')}
    />
  );
}
