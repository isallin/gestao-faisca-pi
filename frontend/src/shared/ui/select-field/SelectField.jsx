import '@/shared/ui/field/Field.css';

/** Select com rótulo. Aceita `options` (lista de strings) e/ou <option> em `children`. */
export function SelectField({ label, options = [], children, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select className="input" {...props}>
        {children}
        {options.map((option) => (
          <option key={option}>{option}</option>
        ))}
      </select>
    </label>
  );
}
