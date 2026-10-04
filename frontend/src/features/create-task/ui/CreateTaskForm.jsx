import { useState } from 'react';
import { useClients } from '@/entities/client';
import { useCollaborators } from '@/entities/collaborator';
import { useProjects } from '@/entities/project';
import { useTasks } from '@/entities/task';
import { readForm } from '@/shared/lib/read-form';
import {
  Field,
  Form,
  FormActions,
  FormAlert,
  FormColumns,
  Modal,
  SelectField,
} from '@/shared/ui';

const STAGES = ['Estudo preliminar', 'Anteprojeto', 'Projeto legal', 'Executivo', 'Obra'];
const CATEGORIES = ['Projeto executivo', 'Obra', 'Acabamentos', 'Orçamento'];

export function CreateTaskForm({ onClose }) {
  const tasks = useTasks();
  const clients = useClients().items;
  const collaborators = useCollaborators().items;
  const projects = useProjects().items;
  const [error, setError] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    if (!data.name) return setError('Preencha o nome da tarefa para continuar.');
    if (data.end < data.start) {
      return setError('Não foi possível salvar: a data de término não pode ser anterior à de início.');
    }

    tasks.add({ id: Date.now(), ...data, done: false });
    onClose();
  };

  return (
    <Modal title="Nova Tarefa" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormColumns cols={2}>
          <div>
            <SelectField label="Membros" name="member" options={collaborators.map((c) => c.name)} />
            <Field label="Nome" name="name" />
            <SelectField label="Cliente" name="client" options={clients.map((c) => c.name)}>
              <option value="">Selecione</option>
            </SelectField>
            <SelectField label="Projeto" name="project" options={projects.map((p) => p.code)} />
            <SelectField label="Etapa" name="stage" options={STAGES} />
            <SelectField label="Status" name="status" options={['Pendente', 'Em andamento']} />
            <SelectField label="Prioridade" name="priority" options={['Baixa', 'Média', 'Alta', 'Crítica']} />
            <SelectField label="Categoria" name="category" options={CATEGORIES} />
          </div>
          <div>
            <Field label="Data de início" name="start" type="date" />
            <Field label="Data do fim" name="end" type="date" />
            <SelectField label="Dependências" name="dependency" options={tasks.items.map((t) => t.name)}>
              <option value="">Nenhuma</option>
            </SelectField>
            <Field label="Observações" name="note" textarea />
            <FormActions label="Salvar tarefa" onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
