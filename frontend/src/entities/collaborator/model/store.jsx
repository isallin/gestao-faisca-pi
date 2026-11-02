import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import { createCollaborator, deleteCollaborator, listCollaborators, updateCollaborator } from '@/shared/api/collaborator';

const CollaboratorsContext = createContext(null);

export function CollaboratorsProvider({ children }) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) return;

    setIsLoading(true);
    setError('');
    try {
      const records = await listCollaborators();
      setItems(records.map(mapCollaboratorResponse));
    } catch {
      setError('Nao foi possivel carregar os colaboradores.');
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
        const created = await createCollaborator(payload);
        const mapped = mapCollaboratorResponse(created);
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: async (id, payload) => {
        const updated = mapCollaboratorResponse(await updateCollaborator(id, payload));
        setItems((list) => list.map((item) => item.id === id ? updated : item));
        return updated;
      },
      remove: async (id) => {
        await deleteCollaborator(id);
        setItems((list) => list.filter((item) => item.id !== id));
      },
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

function mapCollaboratorResponse(item) {
  return {
    id: item.id,
    name: `${item.nome} ${item.sobrenome}`.trim(),
    role: item.cargo,
    projects: '',
    email: item.email,
  };
}
