import { Field } from '@/shared/ui/field';

/** Bloco de endereço reutilizável (clientes e projetos). */
export function AddressFields() {
  return (
    <div>
      <h3>Endereço</h3>
      <Field label="Logradouro" name="street" />
      <Field label="Número" name="number" />
      <Field label="Complemento" name="complement" />
      <Field label="CEP" name="zip" />
      <Field label="Cidade" name="city" />
      <Field label="Estado" name="state" />
      <Field label="País" name="country" defaultValue="Brasil" />
    </div>
  );
}
