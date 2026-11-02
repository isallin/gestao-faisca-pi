import { useState } from 'react';
import axios from 'axios';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
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
const PROPERTY = ['Residencial', 'Comercial', 'Industrial', 'Rural', 'Terreno', 'Outro'];

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
  const { add, update, phases } = useProjects();
  const clients = useClients().items;
  const isEditing = Boolean(project);
  const d = detailsOf(project);
  const [error, setError] = useState('');
  const defaultClientId = String(
    project?.clientId ?? clients.find((client) => client.name === project?.client)?.id ?? clients[0]?.id ?? '',
  );
  const defaultPhaseId = String(project?.stageId ?? phases[0]?.id ?? '');

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    const clientId = Number(data.clientId || project?.clientId || clients[0]?.id || 0);
    const phaseId = Number(data.phaseId || project?.stageId || phases[0]?.id || 0) || null;
    const clientName = clients.find((client) => client.id === clientId)?.name ?? project?.client;
    const phaseName = phases.find((phase) => phase.id === phaseId)?.nome ?? project?.stage;
    const address = `${data.neighborhood ? `${data.neighborhood}, ` : ''}${data.city}-${data.state}`;
    if (!data.name) return setError('Preencha o nome do projeto para continuar.');
    if (data.end < data.start) {
      return setError('Não foi possível salvar: a data de término não pode ser anterior à de início.');
    }

    const payload = {
      clienteId: clientId,
      codigo: data.code,
      relacaoProprietario: '',
      nome: data.name || 'Projeto sem nome',
      tipoImovel: data.propertyType,
      areaM2: data.area ? Number(data.area) : null,
      unidadeNumero: data.number,
      condominio: data.condo,
      matricula: data.registry,
      dataInicio: data.start ? `${data.start}T00:00:00` : null,
      dataFim: data.end ? `${data.end}T00:00:00` : null,
      observacao: data.note,
      fkTarefaFase: phaseId,
      status: data.status,
      stage: phaseName,
      address,
      clientName,
      details: data,
    };

    setError('');

    if (!data.name || !data.code || !clientId) {
      setError('Preencha nome, codigo e cliente para continuar.');
      return;
    }

    if (!phaseId) {
      setError('Selecione uma fase para continuar.');
      return;
    }

    try {
      if (isEditing) await update(project.id, payload);
      else await add(payload);
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
            <SelectField label="Cliente" name="clientId" defaultValue={defaultClientId || ""}>
              <option value="" disabled>Selecione...</option>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.name}
                </option>
              ))}
            </SelectField>
            <SelectField label="Tipo de imóvel" name="propertyType" defaultValue={d.propertyType} options={PROPERTY}>
              <option value="" disabled>Selecione...</option>
            </SelectField>
            <Field label="Metragem (m²)" name="area" placeholder="Ex.: 180" defaultValue={d.area} />
            <Field label="Condomínio" name="condo" placeholder="Nome do condomínio (se houver)" defaultValue={d.condo} />
            <Field label="Matrícula" name="registry" placeholder="Nº da matrícula do imóvel" defaultValue={d.registry} />
          </div>
          <AddressFields defaults={d} />
          <div>
            <h3>Status e observação</h3>
            <SelectField label="Fase" name="phaseId" defaultValue={defaultPhaseId}>
              <option value="" disabled>Selecione...</option>
              {phases.map((phase) => (
                <option key={phase.id} value={phase.id}>
                  {phase.nome}
                </option>
              ))}
            </SelectField>
            <Field label="Data de início" name="start" type="date" defaultValue={project?.start} />
            <Field label="Data do fim" name="end" type="date" defaultValue={project?.end} />
            <Field label="Observação" name="note" textarea placeholder="Informações adicionais sobre o projeto" defaultValue={project?.note} />
            <FormActions label={isEditing ? 'Salvar alterações' : 'Salvar projeto'} onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
