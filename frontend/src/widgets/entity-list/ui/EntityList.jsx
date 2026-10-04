import { useState } from 'react';
import { ChevronDown, ChevronUp, Pencil } from 'lucide-react';
import { Button, CheckRail, FilterPanel, PageHeader } from '@/shared/ui';
import './EntityList.css';

/**
 * Lista expansível com busca, usada nas telas de clientes, colaboradores,
 * fornecedores e projetos.
 *
 * @param {{
 *   title: string, addLabel: string, rows: Array<{id:number}>,
 *   columns: Array<{key:string,label:string}>,
 *   detail: (row:object) => Array<{label:string,value:string}>,
 *   onAdd: () => void
 * }} props
 */
export function EntityList({ title, addLabel, rows, columns, detail, onAdd }) {
  const [expandedId, setExpandedId] = useState();
  const [filterOpen, setFilterOpen] = useState(false);
  const [term, setTerm] = useState('');

  const visible = rows.filter((row) =>
    Object.values(row).join(' ').toLowerCase().includes(term.toLowerCase()),
  );

  return (
    <>
      <PageHeader
        title={title}
        action={
          <Button variant="secondary" onClick={onAdd}>
            + {addLabel}
          </Button>
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
                    <strong>{String(row[column.key] || '—')}</strong>
                  </div>
                ))}
                <Button variant="ghost" aria-expanded={isOpen} onClick={() => setExpandedId(isOpen ? undefined : row.id)}>
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
              </div>

              {isOpen && (
                <div className="entity-detail">
                  <Button variant="ghost">
                    <Pencil size={15} /> Editar
                  </Button>
                  {detail(row).map((item) => (
                    <div key={item.label}>
                      <small>{item.label}</small>
                      <p>{item.value || 'Não informado'}</p>
                    </div>
                  ))}
                </div>
              )}

              <CheckRail label={`Selecionar ${String(row[columns[0].key])}`} />
            </article>
          );
        })}
        {visible.length === 0 && (
          <div className="empty">Nenhum(a) {title.toLowerCase()} cadastrado(a) ainda.</div>
        )}
      </section>
    </>
  );
}
