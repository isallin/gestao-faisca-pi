import { useState } from 'react';
import { ChevronDown, ChevronUp, Pencil } from 'lucide-react';
import { Button, FilterPanel, PageHeader } from '@/shared/ui';
import { ALL, applyFilters, initialFilterValues } from '../lib/filters';
import './EntityList.css';

/**
 * Lista expansível com busca e filtros, usada nas telas de clientes, colaboradores,
 * fornecedores, projetos e tarefas.
 *
 * @param {{
 *   title: string, addLabel: string, rows: Array<{id:number}>,
 *   columns: Array<{key:string,label:string, render?: (row:object) => string}>,
 *   detail: (row:object) => Array<{label:string,value:string}>,
 *   onAdd?: () => void,  // sem onAdd, o botão "+ Adicionar" não é exibido
 *   onEdit?: (row:object) => void,
 *   filters?: Array<{key:string,label:string,options:Array<{value:string,label:string}>,match:(row:object,value:string)=>boolean}>,
 *   filterValues?: Record<string,string>, onFilterChange?: (key:string, value:string) => void,
 *   headerExtra?: import('react').ReactNode,
 *   emptyMessage?: string,
 *   detailFooter?: (row:object) => import('react').ReactNode
 * }} props
 */
export function EntityList({
  title,
  addLabel,
  rows,
  columns,
  detail,
  onAdd,
  onEdit,
  filters = [],
  filterValues,
  onFilterChange,
  headerExtra,
  detailFooter,
  emptyMessage,
}) {
  const [expandedId, setExpandedId] = useState();
  const [filterOpen, setFilterOpen] = useState(false);
  const [term, setTerm] = useState('');
  const [ownValues, setOwnValues] = useState(() => initialFilterValues(filters));

  const values = filterValues ?? ownValues;
  const changeFilter =
    onFilterChange ?? ((key, value) => setOwnValues((v) => ({ ...v, [key]: value })));

  const lowerTerm = term.toLowerCase();
  const visible = applyFilters(rows, filters, values).filter((row) =>
    Object.values(row).join(' ').toLowerCase().includes(lowerTerm),
  );

  return (
    <>
      <PageHeader
        title={title}
        action={
          <>
            {onAdd && (
              <Button variant="secondary" onClick={onAdd}>
                + {addLabel}
              </Button>
            )}
            {headerExtra}
          </>
        }
        onFilter={() => setFilterOpen((v) => !v)}
      />

      {filterOpen && (
        <FilterPanel>
          <label>
            Buscar
            <input
              className="input"
              value={term}
              onChange={(e) => setTerm(e.target.value)}
              placeholder={`Buscar em ${title.toLowerCase()}`}
            />
          </label>
          {filters.map((filter) => (
            <label key={filter.key}>
              {filter.label}
              <select
                className="input"
                value={values[filter.key] ?? ALL}
                onChange={(e) => changeFilter(filter.key, e.target.value)}
              >
                {filter.options.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>
          ))}
        </FilterPanel>
      )}

      <section className="list-panel" aria-label={`Lista de ${title}`}>
        {visible.map((row) => {
          const isOpen = expandedId === row.id;
          return (
            <article className="entity-card" key={row.id}>
              <div className="entity-main">
                {columns.map((column) => (
                  <div key={column.key}>
                    <small>{column.label}</small>
                    <strong>
                      {String((column.render ? column.render(row) : row[column.key]) || '—')}
                    </strong>
                  </div>
                ))}
                <Button
                  variant="ghost"
                  aria-expanded={isOpen}
                  onClick={() => setExpandedId(isOpen ? undefined : row.id)}
                >
                  {isOpen ? (
                    <>
                      <ChevronUp size={16} /> Ver menos
                    </>
                  ) : (
                    <>
                      <ChevronDown size={16} /> Ver mais
                    </>
                  )}
                </Button>
                {onEdit && (
                  <Button variant="ghost" onClick={() => onEdit(row)}>
                    <Pencil size={15} /> Editar
                  </Button>
                )}
              </div>

              {isOpen && (
                <div className="entity-detail">
                  {detail(row).map((item) => (
                    <div key={item.label}>
                      <small>{item.label}</small>
                      <p>{item.value || 'Não informado'}</p>
                    </div>
                  ))}
                  {detailFooter && <div className="entity-detail-footer">{detailFooter(row)}</div>}
                </div>
              )}
            </article>
          );
        })}
        {visible.length === 0 && (
          <div className="empty">
            {emptyMessage ?? `Nenhum(a) ${title.toLowerCase()} encontrado(a).`}
          </div>
        )}
      </section>
    </>
  );
}
