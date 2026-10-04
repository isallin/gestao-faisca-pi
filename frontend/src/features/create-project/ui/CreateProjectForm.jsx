import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
import { readForm } from '@/shared/lib/read-form';
import {
  AddressFields,
  Field,
  Form,
  FormActions,
  FormColumns,
  Modal,
  SelectField,
} from '@/shared/ui';

const STATUSES = ['Planejamento', 'Em andamento', 'Aguardando', 'Concluído'];
const STAGES = ['Estudo preliminar', 'Anteprojeto', 'Projeto legal', 'Executivo', 'Obra', 'Entrega'];

export function CreateProjectForm({ onClose }) {
  const { add } = useProjects();
  const clients = useClients().items;

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    add({
      id: Date.now(),
      name: data.name,
      code: data.code,
      client: data.client,
      status: data.status,
      stage: data.stage,
      address: `${data.city}-${data.state}`,
      note: data.note,
    });
    onClose();
  };

  return (
    <Modal title="Novo Projeto" size="large" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormColumns cols={3}>
          <div>
            <h3>Dados do projeto</h3>
            <Field label="Nome" name="name" />
            <Field label="Sigla/Código" name="code" />
            <SelectField label="Cliente" name="client" options={clients.map((c) => c.name)} />
            <Field label="Tipo de imóvel" name="propertyType" />
            <Field label="Metragem (m²)" name="area" />
            <Field label="Condomínio" name="condo" />
            <Field label="Matrícula" name="registry" />
          </div>
          <AddressFields />
          <div>
            <h3>Status e observação</h3>
            <SelectField label="Status" name="status" options={STATUSES} />
            <SelectField label="Etapa" name="stage" options={STAGES} />
            <Field label="Observação" name="note" textarea />
            <FormActions label="Salvar projeto" onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
