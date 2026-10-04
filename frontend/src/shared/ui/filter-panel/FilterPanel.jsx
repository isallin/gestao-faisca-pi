import { cn } from '@/shared/lib/cn';
import './FilterPanel.css';

export function FilterPanel({ open = true, className, children }) {
  return <div className={cn('filter-panel', !open && 'filter-closed', className)}>{children}</div>;
}
