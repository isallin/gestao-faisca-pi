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

/** Valores iniciais a partir de um projeto existente (edição). */
function detailsOf(project) {
  if (!project) return {};
  if (project.details) return project.details;
  const index = (project.address ?? '').lastIndexOf('-');
  return {
    city: index > 0 ? project.address.slice(0, index) : project.address,
    state: index > 0 ? project.address.slice(index + 1) : '',
  };
}

/** project: quando informado, o formulário entra em modo de edição. */
export function CreateProjectForm({ onClose, project }) {
  const { add, update } = useProjects();
  const clients = useClients().items;
  const isEditing = Boolean(project);
  const d = detailsOf(project);

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    const record = {
      name: data.name,
      code: data.code,
      client: data.client,
      status: data.status,
      stage: data.stage,
      address: `${data.neighborhood ? `${data.neighborhood}, ` : ''}${data.city}-${data.state}`,
      note: data.note,
      details: data,
    };
    if (isEditing) update(project.id, record);
    else add({ id: Date.now(), ...record });
    onClose();
  };

  return (
    <Modal title={isEditing ? 'Editar Projeto' : 'Novo Projeto'} size="large" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormColumns cols={3}>
          <div>
            <h3>Dados do projeto</h3>
            <Field label="Nome" name="name" placeholder="Ex.: Casa Paineiras" defaultValue={project?.name} />
            <Field label="Sigla/Código" name="code" placeholder="Ex.: RES-024" defaultValue={project?.code} />
            <SelectField label="Cliente" name="client" options={clients.map((c) => c.name)} defaultValue={project?.client} />
            <Field label="Tipo de imóvel" name="propertyType" placeholder="Ex.: Residência" defaultValue={d.propertyType} />
            <Field label="Metragem (m²)" name="area" placeholder="Ex.: 180" defaultValue={d.area} />
            <Field label="Condomínio" name="condo" placeholder="Nome do condomínio (se houver)" defaultValue={d.condo} />
            <Field label="Matrícula" name="registry" placeholder="Nº da matrícula do imóvel" defaultValue={d.registry} />
          </div>
          <AddressFields defaults={d} />
          <div>
            <h3>Status e observação</h3>
            <SelectField label="Status" name="status" options={STATUSES} defaultValue={project?.status} />
            <SelectField label="Etapa" name="stage" options={STAGES} defaultValue={project?.stage} />
            <Field label="Observação" name="note" textarea placeholder="Informações adicionais sobre o projeto" defaultValue={project?.note} />
            <FormActions label={isEditing ? 'Salvar alterações' : 'Salvar projeto'} onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
