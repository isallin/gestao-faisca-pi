import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCollaborators } from '@/entities/collaborator';
import { useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { Button } from '@/shared/ui';
import { EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Colaborador' },
  { key: 'role', label: 'Cargo' },
  { key: 'projects', label: 'Projetos envolvidos' },
];

const detail = (person) => [
  { label: 'E-mail', value: person.email },
];

export function CollaboratorsPage() {
  usePageTitle('Colaboradores');
  const navigate = useNavigate();
  const { items, isLoading, error, reload } = useCollaborators();
  const { items: tasks } = useTasks();

  const rows = useMemo(() => {
    const projectsByMemberId = new Map();
    for (const task of tasks) {
      if (!task.project) continue;
      const memberIds =
        Array.isArray(task.collaboratorIds) && task.collaboratorIds.length > 0
          ? task.collaboratorIds
          : task.memberId != null
            ? [task.memberId]
            : [];
      for (const memberId of memberIds) {
        if (!projectsByMemberId.has(memberId)) projectsByMemberId.set(memberId, new Set());
        projectsByMemberId.get(memberId).add(task.project);
      }
    }

    return items.map((person) => {
      if (person.projects) return person;
      const derived = projectsByMemberId.get(person.id);
      if (!derived || derived.size === 0) return person;
      return {
        ...person,
        projects: [...derived].join(', '),
      };
    });
  }, [items, tasks]);

  return (
    <EntityList
      title="Colaboradores"
      addLabel="Adicionar Colaborador"
      rows={rows}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-colaborador')}
      onEdit={(person) => navigate(`/editar-colaborador/${person.id}`)}
      onDelete={(person) => navigate(`/excluir-colaborador/${person.id}`)}
      headerExtra={
        <Button variant="ghost" onClick={reload}>
          Recarregar
        </Button>
      }
      emptyMessage={
        isLoading
          ? 'Carregando colaboradores...'
          : error || 'Nenhum colaborador encontrado.'
      }
    />
  );
}
