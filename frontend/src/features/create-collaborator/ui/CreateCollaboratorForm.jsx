import { useState } from 'react';
import { useCollaborators } from '@/entities/collaborator';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormAlert, FormPair, Modal } from '@/shared/ui';

/** collaborator: quando informado, o formulário entra em modo de edição. */
export function CreateCollaboratorForm({ onClose, collaborator }) {
  const { add, update } = useCollaborators();
  const [error, setError] = useState('');
  const isEditing = Boolean(collaborator);
  const [first = '', ...rest] = (collaborator?.name ?? '').split(' ');

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);

    if (data.password !== data.confirm) {
      return setError('Não foi possível salvar: as senhas não coincidem.');
    }

    const record = {
      name: `${data.name} ${data.last}`.trim(),
      role: data.role,
      email: data.email,
    };

    if (isEditing) update(collaborator.id, record);
    else add({ id: Date.now(), projects: '', ...record });
    onClose();
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
