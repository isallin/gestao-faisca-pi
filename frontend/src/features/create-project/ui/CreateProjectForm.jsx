import { useState } from 'react';
import axios from 'axios';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
import { getAuthToken } from '@/shared/api/auth';
import { readForm } from '@/shared/lib/read-form';
import {
  AddressFields,
  Field,
  Form,
  FormActions,
  FormAlert,
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
  const [error, setError] = useState('');
  const defaultClientId = String(
    project?.clientId ?? clients.find((client) => client.name === project?.client)?.id ?? clients[0]?.id ?? '',
  );

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    const clientId = Number(data.clientId || project?.clientId || clients[0]?.id || 0);
    const clientName = clients.find((client) => client.id === clientId)?.name ?? project?.client;
    const address = `${data.neighborhood ? `${data.neighborhood}, ` : ''}${data.city}-${data.state}`;

    const payload = {
      clienteId: clientId,
      codigo: data.code || `PRJ-${Date.now()}`,
      relacaoProprietario: '',
      nome: data.name || 'Projeto sem nome',
      tipoImovel: data.propertyType,
      areaM2: data.area ? Number(data.area) : null,
      unidadeNumero: data.number,
      condominio: data.condo,
      matricula: data.registry,
      dataInicio: null,
      dataFim: null,
      observacao: data.note,
      fkTarefaFase: null,
      status: data.status,
      stage: data.stage,
      address,
      clientName,
      details: data,
    };

    setError('');

    if (!getAuthToken()) {
      if (isEditing) {
        update(project.id, {
          name: data.name,
          code: data.code,
          client: clientName,
          clientId,
          status: data.status,
          stage: data.stage,
          address,
          note: data.note,
          details: data,
        });
      } else {
        add(payload);
      }
      onClose();
      return;
    }

    if (!data.name || !data.code || !clientId) {
      setError('Preencha nome, codigo e cliente para continuar.');
      return;
    }

    if (isEditing) {
      update(project.id, {
        name: data.name,
        code: data.code,
        client: clientName,
        clientId,
        status: data.status,
        stage: data.stage,
        address,
        note: data.note,
        details: data,
      });
      onClose();
      return;
    }

    try {
      await add(payload);
      onClose();
    } catch (requestError) {
      if (axios.isAxiosError(requestError) && requestError.response?.status === 404) {
        setError('Cliente nao encontrado no backend.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 400) {
        setError('Dados invalidos. Confira os campos e tente novamente.');
      } else {
        setError('Nao foi possivel salvar o projeto no momento.');
      }
    }
  };

  return (
    <Modal title={isEditing ? 'Editar Projeto' : 'Novo Projeto'} size="large" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormColumns cols={3}>
          <div>
            <h3>Dados do projeto</h3>
            <Field label="Nome" name="name" placeholder="Ex.: Casa Paineiras" defaultValue={project?.name} />
            <Field label="Sigla/Código" name="code" placeholder="Ex.: RES-024" defaultValue={project?.code} />
            <SelectField label="Cliente" name="clientId" defaultValue={defaultClientId}>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.name}
                </option>
              ))}
            </SelectField>
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
