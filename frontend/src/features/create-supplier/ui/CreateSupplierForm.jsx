import { useProjects } from '@/entities/project';
import { useSuppliers } from '@/entities/supplier';
import { readForm } from '@/shared/lib/read-form';
import { Field, Form, FormActions, FormColumns, Modal, SelectField } from '@/shared/ui';

const CATEGORIES = ['Marcenaria', 'Iluminação', 'Marmoraria', 'Paisagismo', 'Construção'];

export function CreateSupplierForm({ onClose }) {
  const { add } = useSuppliers();
  const projects = useProjects().items;

  const handleSubmit = (event) => {
    event.preventDefault();
    const { phone2: _p2, email2: _e2, ...data } = readForm(event.currentTarget);
    add({ id: Date.now(), ...data });
    onClose();
  };

  return (
    <Modal title="Novo Fornecedor" onClose={onClose}>
      <Form onSubmit={handleSubmit}>
        <FormColumns cols={2}>
          <div>
            <Field label="Nome" name="name" />
            <SelectField label="Categoria" name="category" options={CATEGORIES} />
            <Field label="Telefone 1" name="phone" />
            <Field label="Telefone 2" name="phone2" />
            <Field label="E-mail 1" name="email" type="email" />
            <Field label="E-mail 2" name="email2" type="email" />
          </div>
          <div>
            <Field label="Cidade" name="city" />
            <SelectField label="Já trabalhei?" name="projects" options={projects.map((p) => p.code)}>
              <option value="">Nenhum projeto</option>
            </SelectField>
            <Field label="Observações" name="note" textarea />
          </div>
        </FormColumns>
        <FormActions label="Salvar fornecedor" onCancel={onClose} pill />
      </Form>
    </Modal>
  );
}
