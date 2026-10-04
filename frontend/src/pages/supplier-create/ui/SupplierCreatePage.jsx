import { useNavigate } from 'react-router-dom';
import { CreateSupplierForm } from '@/features/create-supplier';
import { usePageTitle } from '@/shared/lib/use-page-title';

export function SupplierCreatePage() {
  usePageTitle('Novo fornecedor');
  const navigate = useNavigate();
  return <CreateSupplierForm onClose={() => navigate('/fornecedores')} />;
}
