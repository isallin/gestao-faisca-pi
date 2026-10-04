import './FormAlert.css';

export function FormAlert({ children }) {
  if (!children) return null;
  return (
    <div className="form-alert" role="alert">
      {children}
    </div>
  );
}
