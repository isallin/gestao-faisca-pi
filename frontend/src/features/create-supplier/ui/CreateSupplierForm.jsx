import { useState } from 'react';
import { IMaskInput } from 'react-imask';
import { useProjects } from '@/entities/project';
import { useSuppliers } from '@/entities/supplier';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormAlert, FormColumns, Modal, SelectField } from '@/shared/ui';

const CATEGORIES = ['Marcenaria', 'Iluminação', 'Marmoraria', 'Paisagismo', 'Construção'];
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

/** supplier: quando informado, o formulário entra em modo de edição. */
export function CreateSupplierForm({ onClose, supplier }) {
  const { add, update, categories: storedCategories = [] } = useSuppliers();
  const projects = useProjects().items;
  const isEditing = Boolean(supplier);
  const [error, setError] = useState('');
  const categoryOptions = [...new Set([...CATEGORIES, ...storedCategories.map((c) => c.nome).filter(Boolean)])];
  const codes = projects.map((p) => p.code);
  // Preserva valores como "RES-024, INT-032" que não existem como opção única.
  const hasCustomProjects = isEditing && supplier.projects && !codes.includes(supplier.projects);

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = readForm(event.currentTarget);
    if (!data.name.trim() || !data.category) return setError('Informe nome e categoria.');
    const payload = {
      ...data,
      jaTrabalhei: Boolean(data.projects && data.projects.trim()),
    };
    try {
      if (isEditing) await update(supplier.id, payload);
      else await add(payload);
      onClose();
    } catch {
      setError('Não foi possível salvar o fornecedor. Verifique os dados e tente novamente.');
    }
  };

  return (
    <Modal title={isEditing ? 'Editar Fornecedor' : 'Novo Fornecedor'} onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormAlert>{error}</FormAlert>
        <FormColumns cols={2}>
          <div>
            <Field label="Nome" name="name" placeholder="Nome do fornecedor" defaultValue={supplier?.name} />
            <SelectField label="Categoria" name="category" options={categoryOptions} defaultValue={supplier?.category} />
            <MaskedField
              label="Telefone 1"
              name="phone"
              placeholder="(11) 90000-0000"
              defaultValue={supplier?.phone}
              mask={PHONE_MASKS}
              dispatch={pickPhoneMask}
            />
            <MaskedField
              label="Telefone 2"
              name="phone2"
              placeholder="(11) 90000-0000 (opcional)"
              defaultValue={supplier?.phone2}
              mask={PHONE_MASKS}
              dispatch={pickPhoneMask}
            />
            <Field label="E-mail 1" name="email" type="email" placeholder="contato@fornecedor.com.br" defaultValue={supplier?.email} />
            <Field label="E-mail 2" name="email2" type="email" placeholder="outro@fornecedor.com.br (opcional)" defaultValue={supplier?.email2} />
          </div>
          <div>
            <Field label="Cidade" name="city" placeholder="Ex.: São Paulo" defaultValue={supplier?.city} />
            <SelectField
              label="Já trabalhei?"
              name="projects"
              options={codes}
              defaultValue={supplier?.projects ?? ''}
            >
              <option value="">Nenhum projeto</option>
              {hasCustomProjects && <option value={supplier.projects}>{supplier.projects}</option>}
            </SelectField>
            <Field label="Observações" name="note" textarea placeholder="Prazos, contatos, condições..." defaultValue={supplier?.note} />
          </div>
        </FormColumns>
        <FormActions label={isEditing ? 'Salvar alterações' : 'Salvar fornecedor'} onCancel={onClose} />
      </Form>
    </Modal>
  );
}
