import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import * as collaboratorApi from '@/shared/api/collaborator';

const CollaboratorsContext = createContext(null);

export function CollaboratorsProvider({ children }) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) {
      setError('');
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      const records = await collaboratorApi.listCollaborators();
      setItems(records.map(mapCollaboratorResponse));
    } catch {
      setError('Não foi possível carregar os colaboradores.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    reload();
  }, [reload]);

  const value = useMemo(
    () => ({
      items,
      isLoading,
      error,
      reload,
      add: async (payload) => {
        const created = await collaboratorApi.createCollaborator(payload);
        const mapped = mapCollaboratorResponse(created);
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: async (id, payload) => {
        const updated = mapCollaboratorResponse(await collaboratorApi.updateCollaborator(id, payload));
        setItems((list) => list.map((item) => (item.id === Number(id) ? updated : item)));
        return updated;
      },
      patch: async (id, payload) => {
        const updated = mapCollaboratorResponse(await collaboratorApi.patchCollaborator(id, payload));
        setItems((list) => list.map((item) => (item.id === Number(id) ? updated : item)));
        return updated;
      },
      remove: async (id) => {
        await collaboratorApi.deleteCollaborator(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
      listTasks: async (id) => collaboratorApi.listCollaboratorTasks(id),
    }),
    [error, isLoading, items, reload],
  );

  return <CollaboratorsContext.Provider value={value}>{children}</CollaboratorsContext.Provider>;
}

export function useCollaborators() {
  const value = useContext(CollaboratorsContext);
  if (!value) throw new Error('CollaboratorsProvider: Provider ausente');
  return value;
}

export function mapCollaboratorResponse(item) {
  const firstName = (item.nome ?? '').trim();
  const lastName = (item.sobrenome ?? '').trim();
  const projectCodes = Array.isArray(item.projetosCodigos) ? item.projetosCodigos : [];
  const projects =
    typeof item.projetos === 'string' && item.projetos.trim()
      ? item.projetos.trim()
      : projectCodes.join(', ');

  return {
    id: item.id,
    firstName,
    lastName,
    name: `${firstName} ${lastName}`.trim(),
    role: item.cargo ?? '',
    projects,
    projectIds: Array.isArray(item.projetosIds) ? item.projetosIds : [],
    projectCodes,
    taskIds: Array.isArray(item.tarefasIds) ? item.tarefasIds : [],
    email: item.email ?? '',
  };
}
