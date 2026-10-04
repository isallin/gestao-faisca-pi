import { Link } from 'react-router-dom';
import { usePageTitle } from '@/shared/lib/use-page-title';
import './NotFoundPage.css';

export function NotFoundPage() {
  usePageTitle('Página não encontrada');
  return (
    <main className="not-found">
      <h1>404</h1>
      <h2>Página não encontrada</h2>
      <p>O endereço que você procura não existe ou foi movido.</p>
      <Link to="/dashboard" className="btn btn-primary">
        Ir para o Dashboard
      </Link>
    </main>
  );
}
