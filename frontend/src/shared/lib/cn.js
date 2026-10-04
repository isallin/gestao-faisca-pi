/** Junta nomes de classe ignorando valores falsy. */
export const cn = (...classes) => classes.filter(Boolean).join(' ');
