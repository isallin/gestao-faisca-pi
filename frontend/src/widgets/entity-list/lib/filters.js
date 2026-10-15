/**
 * Definição de filtro: { key, label, options: [{ value, label }], match: (row, value) => boolean }
 * O valor 'all' significa "sem filtro" para aquela chave.
 */
export const ALL = 'all';

export const initialFilterValues = (filters = []) =>
  Object.fromEntries(filters.map((f) => [f.key, ALL]));

export const applyFilters = (rows, filters = [], values = {}) =>
  rows.filter((row) =>
    filters.every((f) => values[f.key] === undefined || values[f.key] === ALL || f.match(row, values[f.key])),
  );
