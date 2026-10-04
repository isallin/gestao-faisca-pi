import { cn } from '@/shared/lib/cn';
import './Field.css';

/** Campo de texto com rótulo. Use `textarea` para campo multilinha. */
export function Field({ label, error, textarea = false, className, ...props }) {
  const Control = textarea ? 'textarea' : 'input';
  return (
    <label className={cn('field', className)}>
      <span>{label}</span>
      <Control
        className={cn('input', textarea && 'textarea', error && 'input-error')}
        {...props}
      />
      {error && <small role="alert">{error}</small>}
    </label>
  );
}
