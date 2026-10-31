import { useEffect, useId, useRef, useState } from 'react';
import { ChevronDown } from 'lucide-react';
import '@/shared/ui/field/Field.css';
import { ALL, selectedOf, summarize, toggleAll, toggleOption } from './selection';
import './MultiSelect.css';

/**
 * Filtro de múltipla escolha com caixas de seleção, no estilo do Excel.
 *
 * `options` pode conter uma opção com o valor `ALL`: ela não vira uma caixa, o rótulo
 * dela é usado como texto de "tudo selecionado" (ex.: "Todos os projetos").
 * `value` é `ALL` (sem filtro) ou a lista de valores marcados.
 */
export function MultiSelect({ label, options, value = ALL, onChange }) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef(null);
  const triggerRef = useRef(null);
  const allRef = useRef(null);
  const id = useId();

  const items = options.filter((option) => option.value !== ALL);
  const allValues = items.map((item) => item.value);
  const allLabel = options.find((option) => option.value === ALL)?.label ?? 'Todos';
  const selected = selectedOf(value, allValues);
  const allChecked = selected.length === allValues.length;
  const partial = selected.length > 0 && !allChecked;

  useEffect(() => {
    if (allRef.current) allRef.current.indeterminate = partial;
  }, [partial, open]);

  useEffect(() => {
    if (!open) return undefined;
    const closeOnOutside = (event) => {
      if (!rootRef.current?.contains(event.target)) setOpen(false);
    };
    document.addEventListener('mousedown', closeOnOutside);
    return () => document.removeEventListener('mousedown', closeOnOutside);
  }, [open]);

  const onKeyDown = (event) => {
    if (event.key === 'Escape' && open) {
      setOpen(false);
      triggerRef.current?.focus();
    }
  };

  return (
    <div className="field multi-select" ref={rootRef} onKeyDown={onKeyDown}>
      <span id={`${id}-label`}>{label}</span>
      <button
        type="button"
        id={`${id}-button`}
        ref={triggerRef}
        className="input multi-trigger"
        aria-haspopup="true"
        aria-expanded={open}
        aria-labelledby={`${id}-label ${id}-button`}
        onClick={() => setOpen((v) => !v)}
      >
        <span>{summarize(value, items, allLabel)}</span>
        <ChevronDown size={16} aria-hidden="true" />
      </button>

      {open && (
        <div className="multi-popover" role="group" aria-label={label}>
          <label className="multi-option multi-option-all">
            <input
              type="checkbox"
              ref={allRef}
              checked={allChecked}
              onChange={() => onChange(toggleAll(value))}
            />
            <span>(Selecionar tudo)</span>
          </label>
          <div className="multi-options">
            {items.map((item) => (
              <label className="multi-option" key={item.value}>
                <input
                  type="checkbox"
                  checked={selected.includes(item.value)}
                  onChange={() => onChange(toggleOption(value, allValues, item.value))}
                />
                <span>{item.label}</span>
              </label>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
