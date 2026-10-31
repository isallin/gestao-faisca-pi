import { useState } from 'react';
import axios from 'axios';
import { Plus, X } from 'lucide-react';
import { useClients } from '@/entities/client';
import { useCollaborators } from '@/entities/collaborator';
import { useProjects } from '@/entities/project';
import { TASK_PRIORITIES, useTasks } from '@/entities/task';
import { getAuthToken } from '@/shared/api/auth';
import { readForm } from '@/shared/lib/read-form';
import {
  Button,
  Field,
  Form,
  FormActions,
  FormAlert,
  FormColumns,
  Modal,
  SelectField,
} from '@/shared/ui';
import './CreateTaskForm.css';

const STAGES = ['Estudo preliminar', 'Anteprojeto', 'Projeto legal', 'Executivo', 'Obra'];
const CATEGORIES = ['Projeto executivo', 'Obra', 'Acabamentos', 'Orçamento'];

/** task: quando informada, o formulário entra em modo de edição. */
export function CreateTaskForm({ onClose, task }) {
  const tasks = useTasks();
  const clients = useClients().items;
  const collaborators = useCollaborators().items;
  const projects = useProjects().items;
  const [error, setError] = useState('');
  const [checklist, setChecklist] = useState(task?.checklist ?? []);
  const isEditing = Boolean(task);
  const statuses = isEditing ? ['Pendente', 'Em andamento', 'Concluído'] : ['Pendente', 'Em andamento'];
  const categoryOptions = tasks.categories.length > 0 ? tasks.categories.map((item) => item.nome) : CATEGORIES;
  const phaseOptions = tasks.phases.length > 0 ? tasks.phases.map((item) => item.nome) : STAGES;

  const addItem = () => setChecklist((list) => [...list, { id: Date.now(), text: '', done: false }]);
  const changeItem = (id, text) => setChecklist((list) => list.map((i) => (i.id === id ? { ...i, text } : i)));
  const removeItem = (id) => setChecklist((list) => list.filter((i) => i.id !== id));

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    if (!data.name) return setError('Preencha o nome da tarefa para continuar.');
    if (data.end < data.start) {
      return setError('Não foi possível salvar: a data de término não pode ser anterior à de início.');
    }

    const project = projects.find((item) => item.code === data.project);
    const client = clients.find((item) => item.name === data.client);
    const category = tasks.categories.find((item) => item.nome === data.category);
    const phase = tasks.phases.find((item) => item.nome === data.stage);

    const payload = {
      projetoId: project?.id,
      clienteId: client?.id ?? project?.clientId,
      categoriaId: category?.id,
      fkTarefaFase: phase?.id,
      nome: data.name,
      prioridade: data.priority,
      dataInicioPrev: data.start ? `${data.start}T00:00:00` : '',
      dataFimPrev: data.end ? `${data.end}T00:00:00` : '',
      dataInicioReal: null,
      dataFimReal: null,
      observacao: data.note,
      status: data.status,
      project: data.project,
      member: data.member,
      category: data.category,
      stage: data.stage,
      checklist: checklist.filter((i) => i.text.trim()),
    };

    const record = {
      ...data,
      done: data.status === 'Concluído',
      checklist: checklist.filter((i) => i.text.trim()),
    };

    if (isEditing) {
      tasks.update(task.id, record);
      onClose();
      return;
    }

    setError('');

    if (!getAuthToken()) {
      tasks.add(payload);
      onClose();
      return;
    }

    if (!payload.projetoId || !payload.categoriaId || !payload.fkTarefaFase) {
      setError('Selecione projeto, categoria e etapa validos para salvar no backend.');
      return;
    }

    try {
      await tasks.add(payload);
      onClose();
    } catch (requestError) {
      if (axios.isAxiosError(requestError) && requestError.response?.status === 400) {
        setError('Dados invalidos. Verifique datas e campos obrigatorios.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 404) {
        setError('Projeto, categoria ou fase nao encontrado(a) no backend.');
      } else {
        setError('Nao foi possivel salvar a tarefa no momento.');
      }
    }
  };

  return (
    <Modal title={isEditing ? 'Editar Tarefa' : 'Nova Tarefa'} onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormColumns cols={2}>
          <div>
            <SelectField label="Responsável" name="member" options={collaborators.map((c) => c.name)} defaultValue={task?.member} />
            <Field label="Nome" name="name" placeholder="Ex.: Revisar planta de iluminação" defaultValue={task?.name} />
            <SelectField label="Cliente" name="client" options={clients.map((c) => c.name)} defaultValue={task?.client ?? ''}>
              <option value="">Selecione</option>
            </SelectField>
            <SelectField label="Projeto" name="project" options={projects.map((p) => p.code)} defaultValue={task?.project} />
            <SelectField label="Etapa" name="stage" options={phaseOptions} defaultValue={task?.stage} />
            <SelectField label="Status" name="status" options={statuses} defaultValue={task?.status} />
            <SelectField label="Prioridade" name="priority" options={TASK_PRIORITIES} defaultValue={task?.priority} />
            <SelectField label="Categoria" name="category" options={categoryOptions} defaultValue={task?.category} />
          </div>
          <div>
            <Field label="Data de início" name="start" type="date" defaultValue={task?.start} />
            <Field label="Data do fim" name="end" type="date" defaultValue={task?.end} />
            <Field label="Observações" name="note" textarea placeholder="Detalhes, contatos, pendências..." defaultValue={task?.note} />

            <fieldset className="checklist-field">
              <legend>Checklist</legend>
              {checklist.map((item, index) => (
                <div className="checklist-row" key={item.id}>
                  <input
                    className="input"
                    value={item.text}
                    placeholder={`Item ${index + 1}`}
                    aria-label={`Item ${index + 1} do checklist`}
                    onChange={(e) => changeItem(item.id, e.target.value)}
                  />
                  <Button variant="icon" aria-label={`Remover item ${index + 1}`} onClick={() => removeItem(item.id)}>
                    <X size={18} />
                  </Button>
                </div>
              ))}
              <Button variant="secondary" onClick={addItem}>
                <Plus size={16} /> Adicionar item
              </Button>
            </fieldset>

            <FormActions label={isEditing ? 'Salvar alterações' : 'Salvar tarefa'} onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
