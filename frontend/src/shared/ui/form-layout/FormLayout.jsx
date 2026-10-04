import { cn } from '@/shared/lib/cn';
import { Button } from '@/shared/ui/button';
import './FormLayout.css';

export function Form({ children, ...props }) {
  return (
    <form className="modal-form" {...props}>
      {children}
    </form>
  );
}

/** Grade de colunas separadas por divisória. cols: 2 | 3 */
export function FormColumns({ cols = 2, children }) {
  return <div className={cn('form-columns', cols === 3 ? 'three' : 'two')}>{children}</div>;
}

export function FormPair({ children }) {
  return <div className="form-pair">{children}</div>;
}

/** `pill`: botão único de largura total (formulários compactos). */
export function FormActions({ label, onCancel, pill = false }) {
  return (
    <div className={cn('form-actions', pill && 'form-actions-full')}>
      {!pill && (
        <Button variant="secondary" onClick={onCancel}>
          Cancelar
        </Button>
      )}
      <Button variant={pill ? 'pill' : 'primary'} type="submit">
        {label}
      </Button>
    </div>
  );
}
