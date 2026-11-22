import { useEffect, useId, useMemo, useRef, useState } from 'react';
import { CalendarDays } from 'lucide-react';
import { endOfMonth, format, parseISO, startOfMonth, subDays } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { DayPicker } from 'react-day-picker';
import { cn } from '@/shared/lib/cn';
import '@/shared/ui/field/Field.css';
import 'react-day-picker/dist/style.css';
import './DatePicker.css';

const ISO_DATE = 'yyyy-MM-dd';
const DISPLAY_DATE = 'dd/MM/yyyy';

const parseDate = (value) => {
  if (!value) return undefined;
  const parsed = parseISO(value);
  return Number.isNaN(parsed.getTime()) ? undefined : parsed;
};

const toIso = (date) => (date ? format(date, ISO_DATE) : '');

const toLabel = (date) => (date ? format(date, DISPLAY_DATE, { locale: ptBR }) : 'Selecionar data');

/** Campo de data com popover usando react-day-picker e valor serializado em input hidden. */
export function DatePickerField({
  label,
  name,
  defaultValue,
  className,
  placeholder = 'Selecionar data',
  required = false,
}) {
  const [open, setOpen] = useState(false);
  const [selected, setSelected] = useState(() => parseDate(defaultValue));
  const rootRef = useRef(null);
  const id = useId();

  useEffect(() => {
    setSelected(parseDate(defaultValue));
  }, [defaultValue]);

  useEffect(() => {
    if (!open) return undefined;
    const closeOnOutside = (event) => {
      if (!rootRef.current?.contains(event.target)) setOpen(false);
    };
    document.addEventListener('mousedown', closeOnOutside);
    return () => document.removeEventListener('mousedown', closeOnOutside);
  }, [open]);

  const month = useMemo(() => selected || new Date(), [selected]);

  return (
    <label className={cn('field date-picker-field', className)} ref={rootRef}>
      <span id={`${id}-label`}>{label}</span>
      <input type="hidden" name={name} value={toIso(selected)} required={required} />

      <button
        type="button"
        id={`${id}-button`}
        className="input date-picker-trigger"
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-labelledby={`${id}-label ${id}-button`}
        onClick={() => setOpen((value) => !value)}
      >
        <span>{selected ? toLabel(selected) : placeholder}</span>
        <CalendarDays size={16} aria-hidden="true" />
      </button>

      {open && (
        <div className="date-picker-popover" role="dialog" aria-modal="false">
          <DayPicker
            mode="single"
            locale={ptBR}
            month={month}
            selected={selected}
            onSelect={(value) => {
              setSelected(value);
              if (value) setOpen(false);
            }}
            showOutsideDays
          />
        </div>
      )}
    </label>
  );
}

const sameDay = (left, right) => toIso(left) === toIso(right);

const summarizeRange = (value) => {
  const from = parseDate(value?.from);
  const to = parseDate(value?.to);
  if (!from) return 'Selecionar data ou intervalo';
  if (!to || sameDay(from, to)) {
    return format(from, DISPLAY_DATE, { locale: ptBR });
  }
  return `${format(from, DISPLAY_DATE, { locale: ptBR })} - ${format(to, DISPLAY_DATE, { locale: ptBR })}`;
};

/** Campo de filtro por data com seleção de dia único ou intervalo. */
export function DateRangeFilterField({ label, value, onChange, className }) {
  const [open, setOpen] = useState(false);
  const rootRef = useRef(null);
  const id = useId();

  const from = parseDate(value?.from);
  const to = parseDate(value?.to);
  const selected = from ? { from, to } : undefined;
  const month = from || new Date();

  useEffect(() => {
    if (!open) return undefined;
    const closeOnOutside = (event) => {
      if (!rootRef.current?.contains(event.target)) setOpen(false);
    };
    document.addEventListener('mousedown', closeOnOutside);
    return () => document.removeEventListener('mousedown', closeOnOutside);
  }, [open]);

  const applyRange = (next) => {
    if (!next?.from) return;
    onChange({ from: toIso(next.from), to: next.to ? toIso(next.to) : undefined });
  };

  return (
    <div className={cn('field date-picker-field', className)} ref={rootRef}>
      <span id={`${id}-label`}>{label}</span>
      <button
        type="button"
        id={`${id}-button`}
        className="input date-picker-trigger"
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-labelledby={`${id}-label ${id}-button`}
        onClick={() => setOpen((state) => !state)}
      >
        <span>{summarizeRange(value)}</span>
        <CalendarDays size={16} aria-hidden="true" />
      </button>

      {open && (
        <div className="date-picker-popover" role="dialog" aria-modal="false">
          <div className="date-presets">
            <button type="button" onClick={() => onChange({ from: toIso(new Date()), to: undefined })}>Hoje</button>
            <button
              type="button"
              onClick={() => {
                const today = new Date();
                onChange({ from: toIso(subDays(today, 6)), to: toIso(today) });
              }}
            >
              Ultimos 7 dias
            </button>
            <button
              type="button"
              onClick={() => {
                const today = new Date();
                onChange({ from: toIso(startOfMonth(today)), to: toIso(endOfMonth(today)) });
              }}
            >
              Mes atual
            </button>
          </div>

          <DayPicker
            mode="range"
            locale={ptBR}
            month={month}
            selected={selected}
            onSelect={applyRange}
            showOutsideDays
            numberOfMonths={2}
          />
        </div>
      )}
    </div>
  );
}
