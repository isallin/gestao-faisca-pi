import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
import { useTasks } from '@/entities/task';
import { useCollaborators } from '@/entities/collaborator';
import { useSuppliers } from '@/entities/supplier';
import { extractApiErrorMessage } from '@/shared/api/http';
import { Button, FormAlert } from '@/shared/ui';

function useRecordsStore() {
  const { items: tasks, removeLog, isLoading } = useTasks();
  const items = tasks.flatMap((task) =>
    (task.timeLogs ?? []).map((log) => ({
      id: log.id,
      name: `${task.name} (${(log.start || log.date || '').slice(0, 10)})`,
    })),
  );
  return { items, remove: (id) => removeLog(Number(id)), isLoading };
}

const kinds = {
  client: { useStore: useClients, back: '/clientes', label: 'cliente' },
  project: { useStore: useProjects, back: '/projetos', label: 'projeto' },
  task: { useStore: useTasks, back: '/tarefa', label: 'tarefa' },
  collaborator: { useStore: useCollaborators, back: '/colaborador', label: 'colaborador' },
  supplier: { useStore: useSuppliers, back: '/fornecedores', label: 'fornecedor' },
  record: { useStore: useRecordsStore, back: '/registros', label: 'registro' },
};

export function EntityDeletePage({ kind }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const { useStore, back, label } = kinds[kind];
  const { items, remove, isLoading } = useStore();
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const record = items.find((item) => item.id === Number(id));

  const confirm = async () => {
    setBusy(true);
    try { await remove(id); navigate(back, { replace: true }); }
    catch (cause) {
      const backendMessage = extractApiErrorMessage(cause);
      setError(cause.response?.status === 409
        ? (backendMessage || `Não é possível excluir este ${label}: existem registros vinculados.`)
        : (backendMessage || `Não foi possível excluir este ${label}. Tente novamente.`));
      setBusy(false);
    }
  };

  return <section className="list-panel">
    <h1>Excluir {label}</h1>
    <FormAlert>{error}</FormAlert>
    {isLoading ? <p>Carregando...</p> : record ? <>
      <p>Deseja excluir {record.name || record.email || `#${id}`} permanentemente?</p>
      <Button onClick={confirm} disabled={busy}>{busy ? 'Excluindo...' : 'Confirmar exclusão'}</Button>
    </> : <p>Registro não encontrado.</p>}
    <Link to={back}>Cancelar e voltar</Link>
  </section>;
}
