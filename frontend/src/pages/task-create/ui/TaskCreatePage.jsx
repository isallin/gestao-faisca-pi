import { useNavigate } from 'react-router-dom';
import { CreateTaskForm } from '@/features/create-task';
import { usePageTitle } from '@/shared/lib/use-page-title';

export function TaskCreatePage() {
  usePageTitle('Nova tarefa');
  const navigate = useNavigate();
  return <CreateTaskForm onClose={() => navigate('/tarefa')} />;
}
