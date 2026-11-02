import { useEffect, useRef, useState } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { useClients } from '@/entities/client';
import { useProjects } from '@/entities/project';
import { useTasks } from '@/entities/task';
import { useCollaborators } from '@/entities/collaborator';
import { useSuppliers } from '@/entities/supplier';

const kinds = {
  client: { useStore: useClients, back: '/clientes' },
  project: { useStore: useProjects, back: '/projetos' },
  task: { useStore: useTasks, back: '/tarefa' },
  collaborator: { useStore: useCollaborators, back: '/colaborador' },
  supplier: { useStore: useSuppliers, back: '/fornecedores' },
};

// Rota de operação explícita, sem acrescentar controles à interface.
export function EntityDeletePage({ kind }) {
  const { id } = useParams();
  const { useStore, back } = kinds[kind];
  const { remove } = useStore();
  const started = useRef(null);
  const [result, setResult] = useState(null);
  useEffect(() => {
    if (started.current === id) return;
    started.current = id;
    remove(id).then(() => setResult({ ok: true }))
      .catch((error) => setResult({ ok: false, error }));
  }, [id, remove]);
  if (!result) return null;
  return <Navigate to={back} replace state={result.ok ? undefined : { deleteError: result.error.response?.data || result.error.message }} />;
}
