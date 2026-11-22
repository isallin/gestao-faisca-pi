import { useEffect, useMemo, useRef, useState } from 'react';
import axios from 'axios';
import { IMaskInput } from 'react-imask';
import { useClients } from '@/entities/client';
import { readForm } from '@/shared/lib/read-form';
import {
  Field,
  SelectField,
  Form,
  FormActions,
  FormAlert,
  FormColumns,
  Modal,
  Segmented,
} from '@/shared/ui';
import './CreateClientForm.css';

const TYPES = [
  { value: 'pf', label: 'Pessoa Física' },
  { value: 'pj', label: 'Pessoa Jurídica' },
];

const PHONE_MASKS = ['(00) 0000-0000', '(00) 00000-0000'];

const pickPhoneMask = (appended, dynamicMasked) => {
  const digits = `${dynamicMasked.value}${appended}`.replace(/\D/g, '');
  return dynamicMasked.compiledMasks[digits.length > 10 ? 1 : 0];
};

function MaskedField({ label, className = 'field', mask, dispatch, ...props }) {
  return (
    <label className={className}>
      <span>{label}</span>
      <IMaskInput className="input" mask={mask} dispatch={dispatch} {...props} />
    </label>
  );
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_REGEX = /^\(?\d{2}\)?\s?9?\d{4}-?\d{4}$/;
const CEP_REGEX = /^\d{5}-?\d{3}$/;
const UF_REGEX = /^[A-Za-z]{2}$/;

const onlyDigits = (value) => String(value ?? '').replace(/\D/g, '');
const blank = (value) => !String(value ?? '').trim();

function isValidCpf(value) {
  const digits = onlyDigits(value);
  if (digits.length !== 11 || /^(\d)\1+$/.test(digits)) return false;

  const calcDigit = (length) => {
    let sum = 0;
    for (let i = 0; i < length; i += 1) sum += Number(digits[i]) * (length + 1 - i);
    const mod = (sum * 10) % 11;
    return mod === 10 ? 0 : mod;
  };

  return calcDigit(9) === Number(digits[9]) && calcDigit(10) === Number(digits[10]);
}

function isValidCnpj(value) {
  const digits = onlyDigits(value);
  if (digits.length !== 14 || /^(\d)\1+$/.test(digits)) return false;

  const calcDigit = (base, factors) => {
    const sum = base
      .split('')
      .reduce((acc, item, index) => acc + Number(item) * factors[index], 0);
    const rest = sum % 11;
    return rest < 2 ? 0 : 11 - rest;
  };

  const d1 = calcDigit(digits.slice(0, 12), [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]);
  const d2 = calcDigit(digits.slice(0, 12) + d1, [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]);

  return d1 === Number(digits[12]) && d2 === Number(digits[13]);
}

function validateClientForm(data, isPerson, shouldSendRepresentative) {
  const fullName = `${data.name ?? ''} ${data.last ?? ''}`.trim();

  if (blank(data.email)) return 'E-mail é obrigatório.';
  if (!EMAIL_REGEX.test(data.email.trim())) return 'E-mail inválido.';
  if (data.email.trim().length > 120) return 'E-mail deve ter no máximo 120 caracteres.';

  if (blank(data.phone)) return 'Telefone é obrigatório.';
  if (!PHONE_REGEX.test(data.phone.trim())) return 'Telefone inválido.';

  if (!blank(data.note) && data.note.trim().length > 300) {
    return 'Observação deve ter no máximo 300 caracteres.';
  }

  if (isPerson) {
    if (blank(fullName)) return 'Nome e sobrenome são obrigatórios para pessoa física.';
    if (blank(data.cpf)) return 'CPF é obrigatório.';
    if (!isValidCpf(data.cpf)) return 'CPF inválido.';
    if (!blank(data.rg) && data.rg.trim().length > 20) return 'RG deve ter no máximo 20 caracteres.';
    if (!blank(data.birth) && new Date(data.birth) >= new Date()) {
      return 'Data de nascimento deve estar no passado.';
    }
    if (!blank(data.marital) && data.marital.trim().length > 30) {
      return 'Estado civil deve ter no máximo 30 caracteres.';
    }
    if (!blank(data.job) && data.job.trim().length > 80) {
      return 'Profissão deve ter no máximo 80 caracteres.';
    }
  } else {
    if (blank(data.company)) return 'Nome fantasia é obrigatório.';
    if (data.company.trim().length > 140) return 'Nome fantasia deve ter no máximo 140 caracteres.';
    if (blank(data.cnpj)) return 'CNPJ é obrigatório.';
    if (!isValidCnpj(data.cnpj)) return 'CNPJ inválido.';
  }

  if (blank(data.zip)) return 'CEP é obrigatório.';
  if (!CEP_REGEX.test(data.zip.trim())) return 'CEP inválido.';
  if (blank(data.street)) return 'Logradouro é obrigatório.';
  if (data.street.trim().length > 200) return 'Logradouro deve ter no máximo 200 caracteres.';
  if (blank(data.number)) return 'Número é obrigatório.';
  if (data.number.trim().length > 10) return 'Número deve ter no máximo 10 caracteres.';
  if (!blank(data.complement) && data.complement.trim().length > 60) {
    return 'Complemento deve ter no máximo 60 caracteres.';
  }
  if (blank(data.neighborhood)) return 'Bairro é obrigatório.';
  if (data.neighborhood.trim().length > 80) return 'Bairro deve ter no máximo 80 caracteres.';
  if (blank(data.city)) return 'Cidade é obrigatória.';
  if (data.city.trim().length > 80) return 'Cidade deve ter no máximo 80 caracteres.';
  if (blank(data.state)) return 'Estado é obrigatório.';
  if (!UF_REGEX.test(data.state.trim())) return 'Use a sigla da UF (ex.: SP).';
  if (!blank(data.country) && data.country.trim().length > 80) {
    return 'País deve ter no máximo 80 caracteres.';
  }

  if (shouldSendRepresentative) {
    if (blank(data.repName)) return 'Nome do representante é obrigatório.';
    if (data.repName.trim().length > 200) {
      return 'Nome do representante deve ter no máximo 200 caracteres.';
    }
    if (!blank(data.repCpf) && !isValidCpf(data.repCpf)) return 'CPF do representante inválido.';
    if (!blank(data.repRole) && data.repRole.trim().length > 80) {
      return 'Cargo do representante deve ter no máximo 80 caracteres.';
    }
    if (!blank(data.repEmail) && !EMAIL_REGEX.test(data.repEmail.trim())) {
      return 'E-mail do representante inválido.';
    }
    if (!blank(data.repEmail) && data.repEmail.trim().length > 120) {
      return 'E-mail do representante deve ter no máximo 120 caracteres.';
    }
    if (!blank(data.repPhone) && !PHONE_REGEX.test(data.repPhone.trim())) {
      return 'Telefone do representante inválido.';
    }
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

/** Valores iniciais do formulário a partir de um cliente existente (edição). */
function detailsOf(client) {
  if (!client) return {};
  if (client.details) return client.details;
  const [first = '', ...rest] = client.name.split(' ');
  return {
    name: first,
    last: rest.join(' '),
    company: client.name,
    phone: client.phone,
    email: client.email,
    note: client.note,
  };
}

/**
 * initialType: 'pf' | 'pj' (aba inicial ao criar)
 * client: quando informado, o formulário entra em modo de edição.
 */
export function CreateClientForm({ onClose, initialType = 'pf', client }) {
  const { add, update } = useClients();
  const isEditing = Boolean(client);
  const [type, setType] = useState(isEditing ? (client.type === 'Pessoa Jurídica' ? 'pj' : 'pf') : initialType);
  const [error, setError] = useState('');
  const isPerson = type === 'pf';
  const d = useMemo(() => detailsOf(client), [client]);
  const [zip, setZip] = useState(d.zip ?? '');
  const [street, setStreet] = useState(d.street ?? '');
  const [neighborhood, setNeighborhood] = useState(d.neighborhood ?? '');
  const [city, setCity] = useState(d.city ?? '');
  const [stateCode, setStateCode] = useState(d.state ?? '');
  const lastZipLookup = useRef('');

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
    const name = isPerson ? `${data.name} ${data.last}`.trim() : data.company;

    if (!name) return setError('Informe o nome do cliente para continuar.');

    const shouldSendRepresentative = Boolean(
      data.repName || data.repCpf || data.repRole || data.repEmail || data.repPhone,
    );

    const validationError = validateClientForm(data, isPerson, shouldSendRepresentative);
    if (validationError) {
      setError(validationError);
      return;
    }

    const payload = {
      tipo: isPerson ? 'PF' : 'PJ',
      email: data.email,
      telefone: data.phone,
      observacao: data.note,
      cep: data.zip,
      logradouro: data.street,
      numero: data.number,
      complemento: data.complement,
      bairro: data.neighborhood,
      cidade: data.city,
      estado: data.state,
      pais: data.country || 'Brasil',
      ...(isPerson
        ? {
          nome: name,
          cpf: data.cpf,
          rg: data.rg,
          dataNascimento: data.birth || null,
          estadoCivil: data.marital,
          profissao: data.job,
        }
        : {
          nomeFantasia: data.company,
          cnpj: data.cnpj,
          representantes: shouldSendRepresentative
            ? [
              {
                nomeCompleto: data.repName,
                cpf: data.repCpf,
                cargo: data.repRole,
                email: data.repEmail,
                telefone: data.repPhone,
              },
            ]
            : null,
        }),
    };

    setError('');

    try {
      if (isEditing) await update(client.id, payload);
      else await add(payload);
      onClose();
    } catch (requestError) {
      if (axios.isAxiosError(requestError) && requestError.response?.status === 409) {
        setError('Ja existe um cliente com este documento (CPF/CNPJ).');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 400) {
        const backendMessage = extractBackendMessage(requestError);
        setError(backendMessage || 'Dados invalidos. Revise os campos obrigatorios e tente novamente.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 401) {
        setError('Sessao expirada. Faca login novamente.');
      } else {
        setError('Nao foi possivel salvar o cliente no momento.');
      }
    }
  };

  return (
    <Modal title={isEditing ? 'Editar Cliente' : 'Novo Cliente'} size="large" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        {!isEditing && <Segmented className="form-tabs" options={TYPES} value={type} onChange={setType} />}
        <FormAlert>{error}</FormAlert>
        <FormColumns cols={3}>
          <div>
            <h3>{isPerson ? 'Dados pessoais' : 'Dados da empresa'}</h3>
            {isPerson ? (
              <>
                <Field label="Nome" name="name" placeholder="Ex.: Marina" defaultValue={d.name} />
                <Field label="Sobrenome" name="last" placeholder="Ex.: Almeida" defaultValue={d.last} />
                <Field label="Data de Nascimento" name="birth" type="date" defaultValue={d.birth} />
                <Field label="RG" name="rg" placeholder="00.000.000-0" defaultValue={d.rg} />
                <MaskedField
                  label="CPF"
                  name="cpf"
                  placeholder="000.000.000-00"
                  defaultValue={d.cpf}
                  mask="000.000.000-00"
                />
                <SelectField
                  label="Estado civil"
                  name="marital"
                  defaultValue={d.marital}
                  options={['Solteiro', 'Casado', 'Separado', 'Divorciado', 'Viúvo']}
                >
                  <option value="">Selecione...</option>
                </SelectField>
                <Field label="Profissão" name="job" placeholder="Ex.: Médica" defaultValue={d.job} />
              </>
            ) : (
              <>
                <Field label="Razão Social" name="company" placeholder="Ex.: Ateliê Aurora Ltda." defaultValue={d.company} />
                <Field label="CNPJ" name="cnpj" placeholder="00.000.000/0000-00" defaultValue={d.cnpj} />
                <MaskedField
                  label="Telefone Comercial"
                  name="phone"
                  placeholder="(11) 90000-0000"
                  defaultValue={d.phone}
                  mask={PHONE_MASKS}
                  dispatch={pickPhoneMask}
                />
                <Field label="E-mail Corporativo" name="email" type="email" placeholder="contato@empresa.com.br" defaultValue={d.email} />
                <Field label="Website" name="website" placeholder="www.empresa.com.br" defaultValue={d.website} />
              </>
            )}
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
            <h3>{isPerson ? 'Contato' : 'Representante legal'}</h3>
            {isPerson ? (
              <>
                <MaskedField
                  label="Telefone"
                  name="phone"
                  placeholder="(11) 90000-0000"
                  defaultValue={d.phone}
                  mask={PHONE_MASKS}
                  dispatch={pickPhoneMask}
                />
                <Field label="E-mail" name="email" type="email" placeholder="nome@email.com" defaultValue={d.email} />
              </>
            ) : (
              <>
                <Field label="Nome completo" name="repName" placeholder="Nome do representante" defaultValue={d.repName} />
                <MaskedField
                  label="CPF"
                  name="repCpf"
                  placeholder="000.000.000-00"
                  defaultValue={d.repCpf}
                  mask="000.000.000-00"
                />
                <Field label="Cargo" name="repRole" placeholder="Ex.: Diretor" defaultValue={d.repRole} />
                <MaskedField
                  label="Telefone"
                  name="repPhone"
                  placeholder="(11) 90000-0000"
                  defaultValue={d.repPhone}
                  mask={PHONE_MASKS}
                  dispatch={pickPhoneMask}
                />
                <Field label="E-mail" name="repEmail" type="email" placeholder="representante@empresa.com.br" defaultValue={d.repEmail} />
              </>
            )}
            <Field label="Observação" name="note" textarea placeholder="Informações adicionais sobre o cliente" defaultValue={d.note} />
            <FormActions label={isEditing ? 'Salvar alterações' : 'Salvar cliente'} onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
