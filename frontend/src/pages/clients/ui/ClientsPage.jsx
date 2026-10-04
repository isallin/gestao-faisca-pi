import { useNavigate } from 'react-router-dom';
import { useClients } from '@/entities/client';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Cliente' },
  { key: 'type', label: 'Pessoa Física ou Jurídica' },
  { key: 'projects', label: 'Projeto(s)' },
];

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

  return (
    <EntityList
      title="Clientes"
      addLabel="Adicionar Cliente"
      rows={items}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-cliente-pessoa-fisica')}
    />
  );
}
