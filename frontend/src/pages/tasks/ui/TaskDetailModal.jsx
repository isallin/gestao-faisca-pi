import { useState } from 'react';
import { Clock3 } from 'lucide-react';
import { formatDate, hoursBetween, totalHours, useTasks } from '@/entities/task';
import { readForm } from '@/shared/lib/read-form';
import { Button, Field, FormAlert, Modal } from '@/shared/ui';

const timeOf = (dateTime) => (dateTime || '').slice(11, 16);
const hoursLabel = (hours) => `${hours.toLocaleString('pt-BR')} h`;

/**
 * Detalhe da tarefa em duas telas dentro do mesmo modal:
 * - "details": dados, observações, checklist e histórico de tempo (+ botão Registrar tempo);
 * - "register": formulário de registro de tempo (início, fim e descrição).
 */
export function TaskDetailModal({ taskId, onClose, startRegistering = false }) {
  const { items, update } = useTasks();
  const task = items.find((t) => t.id === taskId);
  const [view, setView] = useState(startRegistering ? 'register' : 'details');
  const [error, setError] = useState('');

  if (!task) return null;

  const logs = task.timeLogs ?? [];
  const checklist = task.checklist ?? [];
  const isRegistering = view === 'register';

  const toggleItem = (itemId) =>
    update(task.id, (t) => ({
      checklist: t.checklist.map((i) => (i.id === itemId ? { ...i, done: !i.done } : i)),
    }));

  const openRegister = () => {
    setError('');
    setView('register');
  };

  const addLog = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    if (!data.start || !data.end) {
      return setError('Informe a data/hora de início e de fim.');
    }
    const hours = hoursBetween(data.start, data.end);
    if (hours <= 0) {
      return setError('A data/hora de fim deve ser posterior à de início.');
    }
    setError('');
    update(task.id, (t) => ({
      timeLogs: [
        ...(t.timeLogs ?? []),
        {
          id: Date.now(),
          date: data.start.slice(0, 10),
          start: data.start,
          end: data.end,
          hours,
          note: data.note,
        },
      ],
    }));
    setView('details');
  };

  return (
    <Modal title={task.name} size="small" onClose={onClose}>
      <div className="task-detail">
        <dl>
          <div>
            <dt>Projeto</dt>
            <dd>{task.project}</dd>
          </div>
          <div>
            <dt>Responsável</dt>
            <dd>{task.member}</dd>
          </div>
          <div>
            <dt>Status</dt>
            <dd>{task.status}</dd>
          </div>
          <div>
            <dt>Prioridade</dt>
            <dd>{task.priority}</dd>
          </div>
          <div>
            <dt>Início</dt>
            <dd>{formatDate(task.start)}</dd>
          </div>
          <div>
            <dt>Fim</dt>
            <dd>{formatDate(task.end)}</dd>
          </div>
        </dl>

        <section className="task-notes">
          <div>
            <h3>Observações</h3>
            <p>{task.note || 'Não informado'}</p>
          </div>
          {!isRegistering && (
            <Button variant="secondary" onClick={openRegister}>
              + Registrar tempo
            </Button>
          )}
        </section>

        {isRegistering ? (
          <section>
            <h3>
              <Clock3 size={18} /> Tempo dedicado: {hoursLabel(totalHours(task))}
            </h3>
            <form className="time-form" onSubmit={addLog} noValidate>
              <FormAlert>{error}</FormAlert>
              <div className="time-form-row">
                <Field label="Data/hora início" name="start" type="datetime-local" />
                <Field label="Data/hora fim" name="end" type="datetime-local" />
              </div>
              <Field label="Descrição" name="note" placeholder="O que foi feito neste período?" />
              <div className="time-form-actions">
                <Button variant="secondary" onClick={() => setView('details')}>
                  Cancelar
                </Button>
                <Button type="submit">Salvar</Button>
              </div>
            </form>
          </section>
        ) : (
          <>
            {checklist.length > 0 && (
              <section>
                <h3>Checklist</h3>
                <ul className="task-checklist">
                  {checklist.map((item) => (
                    <li key={item.id}>
                      <label>
                        <input
                          type="checkbox"
                          checked={item.done}
                          onChange={() => toggleItem(item.id)}
                        />
                        <span className={item.done ? 'checked' : ''}>{item.text}</span>
                      </label>
                    </li>
                  ))}
                </ul>
              </section>
            )}

            <section>
              <h3>
                <Clock3 size={18} /> Tempo dedicado: {hoursLabel(totalHours(task))}
              </h3>
              {logs.length > 0 && (
                <ul className="time-logs">
                  {logs.map((log) => (
                    <li key={log.id}>
                      <strong>
                        {formatDate(log.date)}
                        {log.start && log.end && (
                          <small>
                            {' '}
                            {timeOf(log.start)}–{timeOf(log.end)}
                          </small>
                        )}
                      </strong>
                      <span>{hoursLabel(log.hours)}</span>
                      <small>{log.note}</small>
                    </li>
                  ))}
                </ul>
              )}
            </section>
          </>
        )}
      </div>
    </Modal>
  );
}
