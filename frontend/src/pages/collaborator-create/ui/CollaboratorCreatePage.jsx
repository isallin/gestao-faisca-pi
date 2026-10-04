import { useNavigate } from 'react-router-dom';
import { CreateCollaboratorForm } from '@/features/create-collaborator';
import { usePageTitle } from '@/shared/lib/use-page-title';

export function CollaboratorCreatePage() {
  usePageTitle('Novo colaborador');
  const navigate = useNavigate();
  return <CreateCollaboratorForm onClose={() => navigate('/colaborador')} />;
}
