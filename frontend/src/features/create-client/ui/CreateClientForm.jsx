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

/** initialType: 'pf' | 'pj' */
export function CreateClientForm({ onClose, initialType = 'pf' }) {
  const { add } = useClients();
  const [type, setType] = useState(initialType);
  const [error, setError] = useState('');
  const isPerson = type === 'pf';

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    const name = isPerson ? `${data.name} ${data.last}`.trim() : data.company;

    if (!name) return setError('Informe o nome do cliente para continuar.');

    add({
      id: Date.now(),
      name,
      type: isPerson ? 'Pessoa Física' : 'Pessoa Jurídica',
      projects: '—',
      address: `${data.street}, ${data.number} — ${data.city}-${data.state}`,
      phone: data.phone,
      email: data.email,
      note: data.note ?? '',
    });
    onClose();
  };

  return (
    <Modal title="Novo Cliente" size="large" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <Segmented className="form-tabs" options={TYPES} value={type} onChange={setType} />
        <FormAlert>{error}</FormAlert>
        <FormColumns cols={3}>
          <div>
            <h3>{isPerson ? 'Dados pessoais' : 'Dados da empresa'}</h3>
            {isPerson ? (
              <>
                <Field label="Nome" name="name" />
                <Field label="Sobrenome" name="last" />
                <Field label="Data de Nascimento" name="birth" type="date" />
                <Field label="RG" name="rg" />
                <Field label="CPF" name="cpf" />
                <Field label="Estado civil" name="marital" />
                <Field label="Profissão" name="job" />
              </>
            ) : (
              <>
                <Field label="Razão Social" name="company" />
                <Field label="CNPJ" name="cnpj" />
                <Field label="Telefone Comercial" name="phone" />
                <Field label="E-mail Corporativo" name="email" type="email" />
                <Field label="Website" name="website" />
              </>
            )}
          </div>

          <AddressFields />

          <div>
            <h3>{isPerson ? 'Contato' : 'Representante legal'}</h3>
            {isPerson ? (
              <>
                <Field label="Telefone" name="phone" />
                <Field label="E-mail" name="email" type="email" />
                <Field label="Observação" name="note" textarea />
              </>
            ) : (
              <>
                <Field label="Nome completo" name="repName" />
                <Field label="CPF" name="repCpf" />
                <Field label="Cargo" name="repRole" />
                <Field label="Telefone" name="repPhone" />
                <Field label="E-mail" name="repEmail" type="email" />
              </>
            )}
            <FormActions label="Salvar cliente" onCancel={onClose} />
          </div>
        </FormColumns>
      </Form>
    </Modal>
  );
}
