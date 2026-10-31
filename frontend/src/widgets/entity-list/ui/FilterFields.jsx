import { MultiSelect } from '@/shared/ui';
import { ALL } from '../lib/filters';

/** Um filtro de múltipla escolha (checkboxes) para cada definição em `filters`. */
export function FilterFields({ filters, values, onChange }) {
  return filters.map((filter) => (
    <MultiSelect
      key={filter.key}
      label={filter.label}
      options={filter.options}
      value={values[filter.key] ?? ALL}
      onChange={(next) => onChange(filter.key, next)}
    />
  ));
}
