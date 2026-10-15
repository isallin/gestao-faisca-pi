import { Field } from '@/shared/ui/field';

/** Bloco de endereço reutilizável (clientes e projetos). `defaults`: valores iniciais (edição). */
export function AddressFields({ defaults = {} }) {
  return (
    <div>
      <h3>Endereço</h3>
      <Field label="Logradouro" name="street" placeholder="Rua, avenida..." defaultValue={defaults.street} />
      <Field label="Número" name="number" placeholder="Ex.: 248" defaultValue={defaults.number} />
      <Field label="Complemento" name="complement" placeholder="Apto, bloco, sala..." defaultValue={defaults.complement} />
      <Field label="CEP" name="zip" placeholder="00000-000" defaultValue={defaults.zip} />
      <Field label="Cidade" name="city" placeholder="Ex.: São Paulo" defaultValue={defaults.city} />
      <Field label="Estado" name="state" placeholder="Ex.: SP" defaultValue={defaults.state} />
      <Field label="País" name="country" defaultValue={defaults.country ?? 'Brasil'} />
    </div>
  );
}
