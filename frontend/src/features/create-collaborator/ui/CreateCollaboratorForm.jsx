import { useState } from 'react';
import axios from 'axios';
import { useCollaborators } from '@/entities/collaborator';
import { extractApiErrorMessage } from '@/shared/api/http';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormAlert, FormPair, Modal } from '@/shared/ui';

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function validateCollaboratorForm(data, isEditing) {
  const nome = (data.name ?? '').trim();
  const sobrenome = (data.last ?? '').trim();
  const email = (data.email ?? '').trim();
  const cargo = (data.role ?? '').trim();
  const senha = data.password ?? '';
  const confirm = data.confirm ?? '';

  if (!nome) return 'Nome é obrigatório.';
  if (nome.length > 70) return 'Nome deve ter no máximo 70 caracteres.';

  if (!sobrenome) return 'Sobrenome é obrigatório.';
  if (sobrenome.length > 45) return 'Sobrenome deve ter no máximo 45 caracteres.';

  if (!email) return 'E-mail é obrigatório.';
  if (!EMAIL_REGEX.test(email)) return 'E-mail inválido.';
  if (email.length > 45) return 'E-mail deve ter no máximo 45 caracteres.';

  if (!cargo) return 'Cargo é obrigatório.';
  if (cargo.length > 45) return 'Cargo deve ter no máximo 45 caracteres.';

  if (!isEditing) {
    if (!senha) return 'Senha é obrigatória.';
    if (senha.length < 8 || senha.length > 72) return 'Senha deve ter entre 8 e 72 caracteres.';
  } else if (senha.length > 0 && (senha.length < 8 || senha.length > 72)) {
    return 'Nova senha deve ter entre 8 e 72 caracteres.';
  }

  if (senha !== confirm) {
    return 'Não foi possível salvar: as senhas não coincidem.';
  }

  return '';
}

/** collaborator: quando informado, o formulário entra em modo de edição. */
export function CreateCollaboratorForm({ onClose, collaborator }) {
  const { add, update } = useCollaborators();
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isEditing = Boolean(collaborator);
  const [fallbackFirst = '', ...fallbackRest] = (collaborator?.name ?? '').split(' ');
  const first = collaborator?.firstName ?? fallbackFirst;
  const last = collaborator?.lastName ?? fallbackRest.join(' ');

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (isSubmitting) return;

    const data = readForm(event.currentTarget);
    const validationError = validateCollaboratorForm(data, isEditing);
    if (validationError) {
      return setError(validationError);
    }

    const basePayload = {
      nome: data.name.trim(),
      sobrenome: data.last.trim(),
      email: data.email.trim().toLowerCase(),
      cargo: data.role.trim(),
    };

    const payload = isEditing
      ? data.password
        ? { ...basePayload, senha: data.password }
        : basePayload
      : { ...basePayload, senha: data.password };

    setError('');
    setIsSubmitting(true);

    try {
      if (isEditing) await update(collaborator.id, payload);
      else await add(payload);
      onClose();
    } catch (requestError) {
      const backendMessage = extractApiErrorMessage(requestError);
      if (axios.isAxiosError(requestError) && requestError.response?.status === 409) {
        setError(backendMessage || 'Já existe um colaborador com este e-mail.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 400) {
        setError(backendMessage || 'Dados inválidos. Confira os campos e tente novamente.');
      } else if (axios.isAxiosError(requestError) && requestError.response?.status === 401) {
        setError('Sessão expirada. Faça login novamente.');
      } else {
        setError(backendMessage || 'Não foi possível salvar o colaborador no momento.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal title={isEditing ? 'Editar Colaborador' : 'Novo Colaborador'} size="small" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormPair>
          <Field label="Nome" name="name" placeholder="Ex.: Camila" defaultValue={first} />
          <Field label="Sobrenome" name="last" placeholder="Ex.: Ferreira" defaultValue={last} />
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
