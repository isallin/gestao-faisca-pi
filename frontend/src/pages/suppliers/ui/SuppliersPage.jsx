import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSuppliers } from '@/entities/supplier';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { ALL, EntityList } from '@/widgets/entity-list';

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

/** "RES-024, INT-032" → ['RES-024', 'INT-032'] */
const projectCodes = (supplier) =>
  String(supplier.projects ?? '')
    .split(',')
    .map((code) => code.trim())
    .filter(Boolean);

/** Valores distintos e ordenados, como opções de filtro. */
const optionsFrom = (values, allLabel) => [
  { value: ALL, label: allLabel },
  ...[...new Set(values.filter(Boolean))]
    .sort((a, b) => a.localeCompare(b, 'pt-BR'))
    .map((value) => ({ value, label: value })),
];

export function SuppliersPage() {
  usePageTitle('Fornecedores');
  const navigate = useNavigate();
  const { items } = useSuppliers();

  // As opções saem dos próprios fornecedores, então novos cadastros já aparecem nos filtros.
  const filters = useMemo(
    () => [
      {
        key: 'category',
        label: 'Categoria',
        options: optionsFrom(
          items.map((s) => s.category),
          'Todas',
        ),
        match: (supplier, value) => supplier.category === value,
      },
      {
        key: 'city',
        label: 'Cidade',
        options: optionsFrom(
          items.map((s) => s.city),
          'Todas',
        ),
        match: (supplier, value) => supplier.city === value,
      },
      {
        key: 'worked',
        label: 'Já trabalhei?',
        options: [
          { value: ALL, label: 'Todos' },
          { value: 'yes', label: 'Sim' },
          { value: 'no', label: 'Não' },
        ],
        match: (supplier, value) => projectCodes(supplier).length > 0 === (value === 'yes'),
      },
      {
        key: 'project',
        label: 'Projeto',
        options: optionsFrom(items.flatMap(projectCodes), 'Todos os projetos'),
        match: (supplier, value) => projectCodes(supplier).includes(value),
      },
    ],
    [items],
  );

  return (
    <EntityList
      title="Fornecedores"
      addLabel="Adicionar Fornecedor"
      rows={items}
      columns={COLUMNS}
      detail={detail}
      onAdd={() => navigate('/add-fornecedor')}
      onEdit={(supplier) => navigate(`/editar-fornecedor/${supplier.id}`)}
      filters={filters}
    />
  );
}
