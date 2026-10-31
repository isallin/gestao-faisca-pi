import { ALL } from '@/shared/ui/multi-select';

/**
 * Definição de filtro: { key, label, options: [{ value, label }], match: (row, value) => boolean }
 *
 * O valor de cada filtro é `ALL` (sem filtro) ou a lista de valores marcados.
 * Com vários valores marcados, a linha aparece se bater com ALGUM deles; entre filtros
 * diferentes, a linha precisa bater com TODOS. Lista vazia = nada marcado = nenhuma linha.
 */
export { ALL };

export const initialFilterValues = (filters = []) =>
  Object.fromEntries(filters.map((f) => [f.key, ALL]));

const passes = (row, filter, value) => {
  if (value === undefined || value === ALL) return true;
  const chosen = Array.isArray(value) ? value : [value];
  return chosen.some((v) => filter.match(row, v));
};

export const applyFilters = (rows, filters = [], values = {}) =>
  rows.filter((row) => filters.every((f) => passes(row, f, values[f.key])));
