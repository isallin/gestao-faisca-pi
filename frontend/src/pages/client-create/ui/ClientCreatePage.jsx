import { useNavigate } from 'react-router-dom';
import { CreateClientForm } from '@/features/create-client';
import { usePageTitle } from '@/shared/lib/use-page-title';

/** type: 'pf' | 'pj' — define a aba inicial do formulário. */
export function ClientCreatePage({ type = 'pf' }) {
  usePageTitle('Novo cliente');
  const navigate = useNavigate();
  return <CreateClientForm initialType={type} onClose={() => navigate('/clientes')} />;
}
