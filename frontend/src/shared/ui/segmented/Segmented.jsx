import { cn } from '@/shared/lib/cn';
import './Segmented.css';

/** Controle segmentado (abas). `options`: [{ value, label }] */
export function Segmented({ options, value, onChange, className, ...props }) {
  return (
    <div className={cn('segments', className)} role="tablist" {...props}>
      {options.map((option) => (
        <button
          key={option.value}
          type="button"
          role="tab"
          aria-selected={value === option.value}
          onClick={() => onChange(option.value)}
        >
          {option.label}
        </button>
      ))}
    </div>
  );
}
