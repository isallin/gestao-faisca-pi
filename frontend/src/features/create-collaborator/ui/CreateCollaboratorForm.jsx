import { useState } from 'react';
import { useCollaborators } from '@/entities/collaborator';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormAlert, FormPair, Modal } from '@/shared/ui';

export function CreateCollaboratorForm({ onClose }) {
  const { add } = useCollaborators();
  const [error, setError] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    if (data.password !== data.confirm) {
      return setError('Não foi possível salvar: as senhas não coincidem.');
    }

    add({
      id: Date.now(),
      name: `${data.name} ${data.last}`.trim(),
      role: data.role,
      email: data.email,
      projects: '',
    });
    onClose();
  };

  return (
    <Modal title="Colaborador novo" size="small" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormPair>
          <Field label="Nome" name="name" />
          <Field label="Sobrenome" name="last" />
        </FormPair>
        <Field label="Email" name="email" type="email" />
        <Field label="Cargo" name="role" />
        <FormPair>
          <Field label="Senha" name="password" type="password" />
          <Field label="Confirmar senha" name="confirm" type="password" />
        </FormPair>
        <FormActions label="Salvar colaborador" onCancel={onClose} pill />
      </Form>
    </Modal>
  );
}
