import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useProjects } from '@/entities/project';
import { formatDate, useTasks } from '@/entities/task';
import { usePageTitle } from '@/shared/lib/use-page-title';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormAlert, FormPair, Modal, SelectField } from '@/shared/ui';
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
  const navigate = useNavigate();
  const { items: tasks, addLog, editLog } = useTasks();
  const { items: projects } = useProjects();
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState('');
  const [modalError, setModalError] = useState('');

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

  const toApiDateTime = (value) => (value?.length === 16 ? `${value}:00` : value);

  const save = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    const start = new Date(data.start);
    const end = new Date(data.end);
    if (!data.taskId || Number.isNaN(start.getTime()) || Number.isNaN(end.getTime()) || end <= start) {
      setModalError('Selecione a tarefa e informe um período válido.');
      return;
    }
    const payload = {
      tarefaId: Number(data.taskId),
      inicio: toApiDateTime(data.start),
      fim: toApiDateTime(data.end),
      descricao: data.note,
    };
    try {
      if (editing.id) await editLog(editing.id, payload);
      else await addLog(payload);
      setEditing(null);
      setModalError('');
    } catch {
      setModalError('Não foi possível salvar o registro.');
    }
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
      onAdd={() => {
        setError('');
        setModalError('');
        setEditing({});
      }}
      onEdit={(record) => {
        setError('');
        setModalError('');
        setEditing(record);
      }}
      onDelete={(record) => navigate(`/excluir-registro/${record.id}`)}
      filters={filters}
      emptyMessage="Nenhum registro de tempo encontrado."
    />
    {editing && <Modal title={editing.id ? 'Editar registro' : 'Novo registro'} onClose={() => setEditing(null)}>
      <Form onSubmit={save}>
        <FormAlert>{modalError}</FormAlert>
        <SelectField label="Tarefa" name="taskId" defaultValue={editing.taskId || ''} required>
          <option value="">Selecione...</option>
          {tasks.map((task) => (
            <option key={task.id} value={task.id}>{task.name} — {task.project}</option>
          ))}
        </SelectField>
        <FormPair>
          <Field label="Início" name="start" type="datetime-local" defaultValue={editing.start?.slice(0, 16)} required />
          <Field label="Fim" name="end" type="datetime-local" defaultValue={editing.end?.slice(0, 16)} required />
        </FormPair>
        <Field label="Descrição" name="note" textarea placeholder="Descreva o que foi realizado" defaultValue={editing.note} />
        <FormActions label="Salvar registro" onCancel={() => setEditing(null)} />
      </Form>
    </Modal>}
    </>
  );
}
