import { useEffect } from 'react';
import { X } from 'lucide-react';
import { cn } from '@/shared/lib/cn';
import { Button } from '@/shared/ui/button';
import './Modal.css';

/** size: small | medium | large */
export function Modal({ title, children, onClose, size = 'medium' }) {
  useEffect(() => {
    const onKey = (event) => event.key === 'Escape' && onClose();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <div className="modal-overlay" role="presentation">
      <section
        className={cn('modal', `modal-${size}`)}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
      >
        <header>
          <h2 id="modal-title">{title}</h2>
          <Button variant="icon" onClick={onClose} aria-label="Fechar">
            <X />
          </Button>
        </header>
        {children}
      </section>
    </div>
  );
}
