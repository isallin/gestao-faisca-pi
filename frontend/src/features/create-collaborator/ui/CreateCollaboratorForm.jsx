import { useState } from 'react';
import axios from 'axios';
import { useCollaborators } from '@/entities/collaborator';
import { getAuthToken } from '@/shared/api/auth';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormAlert, FormPair, Modal } from '@/shared/ui';

/** collaborator: quando informado, o formulário entra em modo de edição. */
export function CreateCollaboratorForm({ onClose, collaborator }) {
  const { add, update } = useCollaborators();
  const [error, setError] = useState('');
  const isEditing = Boolean(collaborator);
  const [first = '', ...rest] = (collaborator?.name ?? '').split(' ');

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    if (data.password !== data.confirm) {
      return setError('Não foi possível salvar: as senhas não coincidem.');
    }

    const payload = {
      nome: data.name,
      sobrenome: data.last,
      email: data.email,
      cargo: data.role,
      senha: data.password,
    };

    const record = {
      name: `${data.name} ${data.last}`.trim(),
      role: data.role,
      email: data.email,
    };

    if (isEditing) {
      update(collaborator.id, record);
      onClose();
      return;
    }

    setError('');

    if (!getAuthToken()) {
      add(payload);
      onClose();
      return;
    }

    try {
      await add(payload);
      onClose();
    } catch (requestError) {
      if (axios.isAxiosError(requestError) && requestError.response?.status === 409) {
        setError('Ja existe um colaborador com este e-mail.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 400) {
        setError('Dados invalidos. Confira os campos e tente novamente.');
      } else {
        setError('Nao foi possivel salvar o colaborador no momento.');
      }
    }
  };

  return (
    <Modal title={isEditing ? 'Editar Colaborador' : 'Novo Colaborador'} size="small" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormPair>
          <Field label="Nome" name="name" placeholder="Ex.: Camila" defaultValue={first} />
          <Field label="Sobrenome" name="last" placeholder="Ex.: Ferreira" defaultValue={rest.join(' ')} />
        </FormPair>
        <Field label="Email" name="email" type="email" placeholder="nome@faisca.arq.br" defaultValue={collaborator?.email} />
        <Field label="Cargo" name="role" placeholder="Ex.: Arquiteta júnior" defaultValue={collaborator?.role} />
        <FormPair>
          <Field label={isEditing ? 'Nova senha' : 'Senha'} name="password" type="password" placeholder={isEditing ? 'Deixe em branco para manter' : 'Crie uma senha'} />
          <Field label="Confirmar senha" name="confirm" type="password" placeholder="Repita a senha" />
        </FormPair>
        <FormActions label={isEditing ? 'Salvar alterações' : 'Salvar colaborador'} onCancel={onClose} />
      </Form>
    </Modal>
  );
}
