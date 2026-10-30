import { useNavigate } from 'react-router-dom';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { ALL, EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Cliente' },
  { key: 'type', label: 'Pessoa Física ou Jurídica' },
  { key: 'projects', label: 'Projeto(s)' },
];

const ACTIVE_STATUS = 'Em andamento';

const TYPE_FILTER = {
  key: 'type',
  label: 'Tipo de cliente',
  options: [
    { value: ALL, label: 'Todos' },
    { value: 'Pessoa Física', label: 'Pessoa Física' },
    { value: 'Pessoa Jurídica', label: 'Pessoa Jurídica' },
  ],
  match: (client, value) => client.type === value,
};

const detail = (client) => [
  { label: 'Endereço completo', value: client.address },
  { label: 'Telefone', value: client.phone },
  { label: 'E-mail', value: client.email },
  { label: 'Observação', value: client.note },
];

export function ClientsPage() {
  usePageTitle('Clientes');
  const navigate = useNavigate();
  const { items } = useClients();
  const { items: projects } = useProjects();

  // Cliente com projeto ativo: algum código em "projects" pertence a um projeto em andamento.
  const activeCodes = new Set(projects.filter((p) => p.status === ACTIVE_STATUS).map((p) => p.code));
  const hasActiveProject = (client) =>
    String(client.projects ?? '')
      .split(',')
      .some((code) => activeCodes.has(code.trim()));

  const filters = [
    TYPE_FILTER,
    {
      key: 'activeProject',
      label: 'Projeto ativo',
      options: [
        { value: ALL, label: 'Todos' },
        { value: 'yes', label: 'Com projeto ativo' },
        { value: 'no', label: 'Sem projeto ativo' },
      ],
      match: (client, value) => hasActiveProject(client) === (value === 'yes'),
    },
  ];

  return (
    <EntityList
      title="Clientes"
      addLabel="Adicionar Cliente"
      rows={items}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-cliente-pessoa-fisica')}
      onEdit={(client) => navigate(`/editar-cliente/${client.id}`)}
      filters={filters}
    />
  );
}
