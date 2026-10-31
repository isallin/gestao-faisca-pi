import { useState } from 'react';
import { useClients } from '@/entities/client';
import { readForm } from '@/shared/lib/read-form';
import {
  AddressFields,
  Field,
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
  const d = detailsOf(client);

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    const name = isPerson ? `${data.name} ${data.last}`.trim() : data.company;

    if (!name) return setError('Informe o nome do cliente para continuar.');

    const record = {
      name,
      type: isPerson ? 'Pessoa Física' : 'Pessoa Jurídica',
      address: `${data.street}, ${data.number} — ${data.neighborhood ? `${data.neighborhood}, ` : ''}${data.city}-${data.state}`,
      phone: data.phone,
      email: data.email,
      note: data.note ?? '',
      details: data,
    };

    if (isEditing) update(client.id, record);
    else add({ id: Date.now(), projects: '—', ...record });
    onClose();
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
                <Field label="CPF" name="cpf" placeholder="000.000.000-00" defaultValue={d.cpf} />
                <Field label="Estado civil" name="marital" placeholder="Ex.: Casada" defaultValue={d.marital} />
                <Field label="Profissão" name="job" placeholder="Ex.: Médica" defaultValue={d.job} />
              </>
            ) : (
              <>
                <Field label="Razão Social" name="company" placeholder="Ex.: Ateliê Aurora Ltda." defaultValue={d.company} />
                <Field label="CNPJ" name="cnpj" placeholder="00.000.000/0000-00" defaultValue={d.cnpj} />
                <Field label="Telefone Comercial" name="phone" placeholder="(11) 0000-0000" defaultValue={d.phone} />
                <Field label="E-mail Corporativo" name="email" type="email" placeholder="contato@empresa.com.br" defaultValue={d.email} />
                <Field label="Website" name="website" placeholder="www.empresa.com.br" defaultValue={d.website} />
              </>
            )}
          </div>

          <AddressFields defaults={d} />

          <div>
            <h3>{isPerson ? 'Contato' : 'Representante legal'}</h3>
            {isPerson ? (
              <>
                <Field label="Telefone" name="phone" placeholder="(11) 90000-0000" defaultValue={d.phone} />
                <Field label="E-mail" name="email" type="email" placeholder="nome@email.com" defaultValue={d.email} />
              </>
            ) : (
              <>
                <Field label="Nome completo" name="repName" placeholder="Nome do representante" defaultValue={d.repName} />
                <Field label="CPF" name="repCpf" placeholder="000.000.000-00" defaultValue={d.repCpf} />
                <Field label="Cargo" name="repRole" placeholder="Ex.: Diretor" defaultValue={d.repRole} />
                <Field label="Telefone" name="repPhone" placeholder="(11) 90000-0000" defaultValue={d.repPhone} />
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
