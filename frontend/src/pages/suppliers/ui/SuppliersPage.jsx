import { useNavigate } from 'react-router-dom';
import { useSuppliers } from '@/entities/supplier';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { EntityList } from '@/widgets/entity-list';

const COLUMNS = [
  { key: 'name', label: 'Fornecedor' },
  { key: 'category', label: 'Categoria' },
  { key: 'projects', label: 'Já trabalhei?' },
];

const detail = (supplier) => [
  { label: 'Cidade', value: supplier.city },
  { label: 'Telefone', value: supplier.phone },
  { label: 'E-mail', value: supplier.email },
  { label: 'Observações', value: supplier.note },
];

export function SuppliersPage() {
  usePageTitle('Fornecedores');
  const navigate = useNavigate();
  const { items } = useSuppliers();

  return (
    <EntityList
      title="Fornecedores"
      addLabel="Adicionar Fornecedor"
      rows={items}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-fornecedor')}
    />
  );
}
