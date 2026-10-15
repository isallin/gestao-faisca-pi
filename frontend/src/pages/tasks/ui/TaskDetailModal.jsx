import { useState } from 'react';
import { Clock3 } from 'lucide-react';
import { formatDate, totalHours, useTasks } from '@/entities/task';
import { readForm } from '@/shared/lib/read-form';
import { Button, Field, FormAlert, Modal } from '@/shared/ui';

/** Detalhe da tarefa + checklist + registro de tempo dedicado. */
export function TaskDetailModal({ taskId, onClose }) {
  const { items, update } = useTasks();
  const task = items.find((t) => t.id === taskId);
  const [error, setError] = useState('');

  if (!task) return null;

  const logs = task.timeLogs ?? [];
  const checklist = task.checklist ?? [];

  const toggleItem = (itemId) =>
    update(task.id, (t) => ({
      checklist: t.checklist.map((i) => (i.id === itemId ? { ...i, done: !i.done } : i)),
    }));

  const addLog = (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const data = readForm(form);
    const hours = Number(data.hours.replace(',', '.'));
    if (!data.date || !(hours > 0)) {
      return setError('Informe a data e uma quantidade de horas maior que zero.');
    }
    setError('');
    update(task.id, (t) => ({
      timeLogs: [...(t.timeLogs ?? []), { id: Date.now(), date: data.date, hours, note: data.note }],
    }));
    form.reset();
  };

  return (
    <Modal title={task.name} size="small" onClose={onClose}>
      <div className="task-detail">
        <dl>
          <div><dt>Projeto</dt><dd>{task.project}</dd></div>
          <div><dt>Responsável</dt><dd>{task.member}</dd></div>
          <div><dt>Status</dt><dd>{task.status}</dd></div>
          <div><dt>Prioridade</dt><dd>{task.priority}</dd></div>
          <div><dt>Início</dt><dd>{formatDate(task.start)}</dd></div>
          <div><dt>Fim</dt><dd>{formatDate(task.end)}</dd></div>
        </dl>

        <section>
          <h3>Observações</h3>
          <p>{task.note || 'Não informado'}</p>
        </section>

        {checklist.length > 0 && (
          <section>
            <h3>Checklist</h3>
            <ul className="task-checklist">
              {checklist.map((item) => (
                <li key={item.id}>
                  <label>
                    <input type="checkbox" checked={item.done} onChange={() => toggleItem(item.id)} />
                    <span className={item.done ? 'checked' : ''}>{item.text}</span>
                  </label>
                </li>
              ))}
            </ul>
          </section>
        )}

        <section>
          <h3>
            <Clock3 size={18} /> Tempo dedicado: {totalHours(task).toLocaleString('pt-BR')} h
          </h3>
          {logs.length > 0 && (
            <ul className="time-logs">
              {logs.map((log) => (
                <li key={log.id}>
                  <strong>{formatDate(log.date)}</strong>
                  <span>{log.hours.toLocaleString('pt-BR')} h</span>
                  <small>{log.note}</small>
                </li>
              ))}
            </ul>
          )}
          <form className="time-form" onSubmit={addLog}>
            <FormAlert>{error}</FormAlert>
            <div className="time-form-row">
              <Field label="Data" name="date" type="date" />
              <Field label="Horas" name="hours" type="number" min="0" step="0.25" placeholder="Ex.: 1,5" />
            </div>
            <Field label="Descrição" name="note" placeholder="O que foi feito neste período?" />
            <Button variant="secondary" type="submit">
              + Registrar tempo
            </Button>
          </form>
        </section>
      </div>
    </Modal>
  );
}
