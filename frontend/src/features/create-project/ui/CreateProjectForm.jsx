import { useEffect, useMemo, useRef, useState } from 'react';
import axios from 'axios';
import { IMaskInput } from 'react-imask';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
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

const PROPERTY = ['Residencial', 'Comercial', 'Industrial', 'Rural', 'Terreno', 'Outro'];
const CEP_REGEX = /^\d{5}-?\d{3}$/;
const UF_REGEX = /^[A-Za-z]{2}$/;

const blank = (value) => !String(value ?? '').trim();
const onlyDigits = (value) => String(value ?? '').replace(/\D/g, '');

function MaskedField({ label, className = 'field', mask, ...props }) {
  return (
    <label className={className}>
      <span>{label}</span>
      <IMaskInput className="input" mask={mask} {...props} />
    </label>
  );
}

function isValidArea(value) {
  if (blank(value)) return true;
  const normalized = String(value).trim().replace(',', '.');
  if (!/^\d+(\.\d{1,2})?$/.test(normalized)) return false;
  const [integer, fraction = ''] = normalized.split('.');
  return integer.length <= 8 && fraction.length <= 2;
}

function validateProjectForm(data, clientId, phaseId) {
  if (blank(data.name)) return 'Nome é obrigatório';
  if (data.name.trim().length > 150) return 'Nome deve ter no máximo 150 caracteres';

  if (blank(data.code)) return 'Código é obrigatório';
  if (data.code.trim().length > 45) return 'Código deve ter no máximo 45 caracteres';

  if (!clientId) return 'Cliente é obrigatório';
  if (clientId <= 0) return 'ID do cliente deve ser positivo';

  if (blank(data.propertyType)) return 'Tipo de imóvel é obrigatório';
  if (data.propertyType.trim().length > 45) {
    return 'Tipo de imóvel deve ter no máximo 45 caracteres';
  }

  if (blank(data.area)) return 'Metragem é obrigatória';
  const normalized = String(data.area).trim().replace(',', '.');
  const area = Number(normalized);
  if (Number.isNaN(area)) return 'Área deve ter até 8 dígitos inteiros e 2 decimais';
  if (area < 0) return 'Área não pode ser negativa';
  if (!isValidArea(normalized)) return 'Área deve ter até 8 dígitos inteiros e 2 decimais';

  if (!blank(data.condo) && data.condo.trim().length > 100) {
    return 'Condomínio deve ter no máximo 100 caracteres';
  }
  if (!blank(data.registry) && data.registry.trim().length > 50) {
    return 'Matrícula deve ter no máximo 50 caracteres';
  }

  if (blank(data.zip)) return 'CEP é obrigatório';
  if (!CEP_REGEX.test(data.zip.trim())) return 'CEP inválido';
  if (blank(data.street)) return 'Logradouro é obrigatório';
  if (data.street.trim().length > 200) return 'Logradouro deve ter no máximo 200 caracteres';
  if (blank(data.number)) return 'Número é obrigatório';
  if (data.number.trim().length > 10) return 'Número deve ter no máximo 10 caracteres';
  if (blank(data.complement)) return 'Complemento é obrigatório';
  if (data.complement.trim().length > 60) return 'Complemento deve ter no máximo 60 caracteres';
  if (blank(data.neighborhood)) return 'Bairro é obrigatório';
  if (data.neighborhood.trim().length > 80) return 'Bairro deve ter no máximo 80 caracteres';
  if (blank(data.city)) return 'Cidade é obrigatória';
  if (data.city.trim().length > 80) return 'Cidade deve ter no máximo 80 caracteres';
  if (blank(data.state)) return 'Estado é obrigatório';
  if (!UF_REGEX.test(data.state.trim())) return 'Use a sigla da UF (ex.: SP)';
  if (blank(data.country)) return 'País é obrigatório';
  if (data.country.trim().length > 80) return 'País deve ter no máximo 80 caracteres';

  if (!phaseId) return 'Fase é obrigatória';
  if (phaseId <= 0) return 'ID de tarefa/fase deve ser positivo';

  if (blank(data.start)) return 'Data de início é obrigatória';
  if (blank(data.end)) return 'Data do fim é obrigatória';
  if (data.end < data.start) return 'Fim anterior ao início';

  if (!blank(data.note) && data.note.trim().length > 300) {
    return 'Observação deve ter no máximo 300 caracteres';
  }

  return '';
}

function extractBackendMessage(requestError) {
  const payload = requestError?.response?.data;
  if (typeof payload === 'string' && payload.trim()) return payload;
  if (payload?.message) return payload.message;
  if (Array.isArray(payload?.errors) && payload.errors.length > 0) {
    const first = payload.errors[0];
    return first?.defaultMessage || first?.message || '';
  }
  if (payload?.errors && typeof payload.errors === 'object') {
    const first = Object.values(payload.errors)[0];
    return Array.isArray(first) ? first[0] : first;
  }
  return '';
}

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
  const { add, update, phases, phaseError, reloadPhases } = useProjects();
  const clients = useClients().items;
  const isEditing = Boolean(project);
  const d = useMemo(() => detailsOf(project), [project]);
  const [error, setError] = useState('');
  const [zip, setZip] = useState(d.zip ?? '');
  const [street, setStreet] = useState(d.street ?? '');
  const [neighborhood, setNeighborhood] = useState(d.neighborhood ?? '');
  const [city, setCity] = useState(d.city ?? '');
  const [stateCode, setStateCode] = useState(d.state ?? '');
  const lastZipLookup = useRef('');
  const retriedPhases = useRef(false);
  const defaultClientId = isEditing
    ? String(project?.clientId ?? clients.find((client) => client.name === project?.client)?.id ?? '')
    : '';
  const defaultPhaseId = isEditing ? String(project?.stageId ?? '') : '';

  useEffect(() => {
    if (!phases.length && !retriedPhases.current) {
      retriedPhases.current = true;
      reloadPhases();
    }
  }, [phases.length, reloadPhases]);

  useEffect(() => {
    setZip(d.zip ?? '');
    setStreet(d.street ?? '');
    setNeighborhood(d.neighborhood ?? '');
    setCity(d.city ?? '');
    setStateCode(d.state ?? '');
    lastZipLookup.current = '';
  }, [d]);

  useEffect(() => {
    const digits = onlyDigits(zip);
    if (digits.length !== 8 || lastZipLookup.current === digits) return;

    lastZipLookup.current = digits;
    let active = true;

    const loadAddress = async () => {
      try {
        const response = await fetch(`https://viacep.com.br/ws/${digits}/json/`);
        const data = await response.json();

        if (!active || data?.erro) {
          setStreet('');
          setNeighborhood('');
          setCity('');
          setStateCode('');
          return;
        }

        setStreet(data?.logradouro || '');
        setNeighborhood(data?.bairro || '');
        setCity(data?.localidade || '');
        setStateCode(data?.uf || '');
      } catch {
        if (!active) return;
        setStreet('');
        setNeighborhood('');
        setCity('');
        setStateCode('');
      }
    };

    loadAddress();
    return () => {
      active = false;
    };
  }, [zip]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    const clientId = Number(data.clientId || 0);
    const phaseId = Number(data.phaseId || 0) || null;
    const clientName = clients.find((client) => client.id === clientId)?.name ?? project?.client;
    const phaseName = phases.find((phase) => phase.id === phaseId)?.nome ?? project?.stage;
    const address = `${data.neighborhood ? `${data.neighborhood}, ` : ''}${data.city}-${data.state}`;

    const validationError = validateProjectForm(data, clientId, phaseId);
    if (validationError) return setError(validationError);

    const trimmedNumber = data.number.trim();
    const payload = {
      clienteId: clientId,
      codigo: data.code.trim(),
      relacaoProprietario: '',
      nome: data.name.trim(),
      tipoImovel: data.propertyType.trim(),
      areaM2: Number(String(data.area).trim().replace(',', '.')),
      unidadeNumero: trimmedNumber,
      numero: trimmedNumber,
      condominio: data.condo,
      matricula: data.registry,
      cep: data.zip.trim(),
      logradouro: data.street.trim(),
      complemento: data.complement.trim(),
      bairro: data.neighborhood.trim(),
      cidade: data.city.trim(),
      estado: data.state.trim().toUpperCase(),
      pais: data.country.trim(),
      dataInicio: `${data.start}T00:00:00`,
      dataFim: `${data.end}T00:00:00`,
      observacao: data.note,
      fkTarefaFase: phaseId,
      status: data.status,
      stage: phaseName,
      address,
      clientName,
      details: data,
    };

    setError('');

    try {
      if (isEditing) await update(project.id, payload);
      else await add(payload);
      onClose();
    } catch (requestError) {
      if (axios.isAxiosError(requestError) && requestError.response?.status === 404) {
        const backendMessage = extractBackendMessage(requestError);
        setError(backendMessage || 'Cliente ou fase não encontrado(a) no backend.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 400) {
        const backendMessage = extractBackendMessage(requestError);
        setError(backendMessage || 'Dados inválidos. Confira os campos e tente novamente.');
      } else {
        setError('Não foi possível salvar o projeto no momento.');
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
            <SelectField key={`${defaultClientId}-${clients.length}`} label="Cliente" name="clientId" defaultValue={defaultClientId}>
              <option value="" disabled>Selecione...</option>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.name}
                </option>
              ))}
            </SelectField>
            <SelectField label="Tipo de imóvel" name="propertyType" defaultValue={d.propertyType ?? ''} options={PROPERTY}>
              <option value="" disabled>Selecione...</option>
            </SelectField>
            <Field label="Metragem (m²)" name="area" placeholder="Ex.: 180" defaultValue={d.area} />
            <Field label="Condomínio" name="condo" placeholder="Nome do condomínio (se houver)" defaultValue={d.condo} />
            <Field label="Matrícula" name="registry" placeholder="Nº da matrícula do imóvel" defaultValue={d.registry} />
          </div>
          <div>
            <h3>Endereço</h3>
            <MaskedField
              label="CEP"
              name="zip"
              placeholder="00000-000"
              value={zip}
              onAccept={(value) => setZip(String(value ?? ''))}
              mask="00000-000"
            />
            <Field
              label="Logradouro"
              name="street"
              placeholder="Rua, avenida..."
              value={street}
              onChange={(event) => setStreet(event.target.value)}
            />
            <Field label="Número" name="number" placeholder="Ex.: 248" defaultValue={d.number} />
            <Field
              label="Complemento"
              name="complement"
              placeholder="Apto, bloco, sala..."
              defaultValue={d.complement}
            />
            <Field
              label="Bairro"
              name="neighborhood"
              placeholder="Ex.: Pinheiros"
              value={neighborhood}
              onChange={(event) => setNeighborhood(event.target.value)}
            />
            <Field
              label="Cidade"
              name="city"
              placeholder="Ex.: São Paulo"
              value={city}
              onChange={(event) => setCity(event.target.value)}
            />
            <Field
              label="Estado"
              name="state"
              placeholder="Ex.: SP"
              value={stateCode}
              onChange={(event) => setStateCode(event.target.value)}
            />
            <Field label="País" name="country" defaultValue={d.country ?? 'Brasil'} />
          </div>
          <div>
            <h3>Status e observação</h3>
            <SelectField key={`${defaultPhaseId}-${phases.length}`} label="Fase" name="phaseId" defaultValue={defaultPhaseId}>
              <option value="" disabled>Selecione...</option>
              {phases.map((phase) => (
                <option key={phase.id} value={phase.id}>
                  {phase.nome}
                </option>
              ))}
            </SelectField>
            {phaseError && (
              <small role="alert">
                {phaseError} <button type="button" onClick={reloadPhases}>Recarregar fases</button>
              </small>
            )}
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
