import { useNavigate } from 'react-router-dom';
import { CreateProjectForm } from '@/features/create-project';
import { usePageTitle } from '@/shared/lib/use-page-title';

export function ProjectCreatePage() {
  usePageTitle('Novo projeto');
  const navigate = useNavigate();
  return <CreateProjectForm onClose={() => navigate('/projetos')} />;
}
