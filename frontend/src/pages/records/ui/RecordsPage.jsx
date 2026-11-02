import { useMemo, useState } from 'react';
import { useProjects } from '@/entities/project';
import { formatDate, useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { readForm } from '@/shared/lib/read-form';
import { Field, FormAlert, Modal, Button } from '@/shared/ui';
import { ALL, EntityList } from '@/widgets/entity-list';
import { collectRecords, formatDateTime, formatHours } from '../lib/records';

const COLUMNS = [
  { key: 'task', label: 'Tarefa' },
  { key: 'project', label: 'Projeto' },
  { key: 'date', label: 'Data', render: (record) => formatDate(record.date) },
  { key: 'hours', label: 'Tempo', render: (record) => formatHours(record.hours) },
];

const detail = (record) => [
  { label: 'Responsável', value: record.member },
  { label: 'Início', value: formatDateTime(record.start) },
  { label: 'Fim', value: formatDateTime(record.end) },
  { label: 'Descrição', value: record.note },
];

export function RecordsPage() {
  usePageTitle('Registros');
  const { items: tasks, addLog, editLog, removeLog } = useTasks();
  const { items: projects } = useProjects();
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState('');

  const records = useMemo(() => collectRecords(tasks), [tasks]);
  const filters = useMemo(
    () => [
      {
        key: 'project',
        label: 'Projeto',
        options: [
          { value: ALL, label: 'Todos os projetos' },
          ...[...new Set(projects.map((p) => p.code))].map((code) => ({
            value: code,
            label: code,
          })),
        ],
        match: (record, value) => record.project === value,
      },
    ],
    [projects],
  );

  const save = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    if (!data.taskId || !data.start || !data.end || data.end <= data.start) {
      setError('Selecione a tarefa e informe um período válido.'); return;
    }
    const payload = { tarefaId: Number(data.taskId), inicio: data.start, fim: data.end, descricao: data.note };
    try {
      if (editing.id) await editLog(editing.id, payload);
      else await addLog(payload);
      setEditing(null); setError('');
    } catch { setError('Não foi possível salvar o registro.'); }
  };

  const remove = async (record) => {
    if (!window.confirm('Excluir este registro de horas?')) return;
    try { await removeLog(record.id); setError(''); }
    catch { setError('Não foi possível excluir o registro.'); }
  };

  return (
    <>
    <FormAlert>{error}</FormAlert>
    <EntityList
      title="Registros"
      rows={records}
      columns={COLUMNS}
      detail={detail}
      addLabel="Adicionar Registro"
      onAdd={() => { setError(''); setEditing({}); }}
      onEdit={(record) => { setError(''); setEditing(record); }}
      onDelete={remove}
      filters={filters}
      emptyMessage="Nenhum registro de tempo encontrado."
    />
    {editing && <Modal title={editing.id ? 'Editar registro' : 'Novo registro'} onClose={() => setEditing(null)}>
      <form onSubmit={save}>
        <FormAlert>{error}</FormAlert>
        <label>Tarefa
          <select className="input" name="taskId" defaultValue={editing.taskId || ''} required>
            <option value="">Selecione...</option>
            {tasks.map((task) => <option key={task.id} value={task.id}>{task.name} — {task.project}</option>)}
          </select>
        </label>
        <Field label="Início" name="start" type="datetime-local" defaultValue={editing.start?.slice(0, 16)} />
        <Field label="Fim" name="end" type="datetime-local" defaultValue={editing.end?.slice(0, 16)} />
        <Field label="Descrição" name="note" defaultValue={editing.note} />
        <Button type="submit">Salvar registro</Button>
      </form>
    </Modal>}
    </>
  );
}
