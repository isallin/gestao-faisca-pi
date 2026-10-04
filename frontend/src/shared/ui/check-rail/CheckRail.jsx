import './CheckRail.css';

/** Faixa lateral com checkbox customizado (usada em listas de cards). */
export function CheckRail({ label, ...inputProps }) {
  return (
    <label className="check-rail">
      <input type="checkbox" aria-label={label} {...inputProps} />
      <span />
    </label>
  );
}
