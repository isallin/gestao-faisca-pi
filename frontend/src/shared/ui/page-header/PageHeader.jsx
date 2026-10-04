import { Filter } from 'lucide-react';
import { Button } from '@/shared/ui/button';
import './PageHeader.css';

export function PageHeader({ title, action, onFilter }) {
  return (
    <header className="page-header">
      <h1>{title}</h1>
      <div className="page-actions">
        {action}
        <Button variant="icon" aria-label="Filtrar" title="Filtrar" onClick={onFilter}>
          <Filter size={20} />
        </Button>
      </div>
    </header>
  );
}
