/** Converte um <form> em objeto simples { campo: 'valor' }. */
export const readForm = (form) =>
  Object.fromEntries([...new FormData(form).entries()].map(([k, v]) => [k, String(v ?? '')]));
