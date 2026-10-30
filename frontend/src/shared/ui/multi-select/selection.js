/**
 * Seleção do filtro de múltipla escolha (estilo Excel).
 * O valor é `ALL` (tudo marcado = sem filtro) ou a lista de valores marcados.
 * Lista vazia = nada marcado.
 */
export const ALL = 'all';

/** Valores marcados, mesmo quando o valor guardado é `ALL` ou um valor único. */
export function selectedOf(value, allValues) {
  if (value === undefined || value === ALL) return allValues;
  return Array.isArray(value) ? value : [value];
}

/** Marcou tudo → volta para `ALL`, para o filtro deixar de ser aplicado. */
export function normalize(selected, allValues) {
  const complete = allValues.length > 0 && allValues.every((v) => selected.includes(v));
  return complete ? ALL : selected;
}

export function toggleOption(value, allValues, option) {
  const current = new Set(selectedOf(value, allValues));
  if (!current.delete(option)) current.add(option);
  return normalize(
    allValues.filter((v) => current.has(v)),
    allValues,
  );
}

/** "Selecionar tudo": se tudo está marcado, desmarca tudo; caso contrário, marca tudo. */
export const toggleAll = (value) => (value === ALL || value === undefined ? [] : ALL);

/** Texto do botão do filtro. */
export function summarize(value, items, allLabel) {
  if (value === undefined || value === ALL) return allLabel;
  const chosen = items.filter((item) => selectedOf(value, []).includes(item.value));
  if (chosen.length === 0) return 'Nenhum';
  if (chosen.length <= 2) return chosen.map((item) => item.label).join(', ');
  return `${chosen.length} selecionados`;
}
