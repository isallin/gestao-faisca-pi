import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import { createCollaborator, listCollaborators } from '@/shared/api/collaborator';
import { initialCollaborators } from './mock';

const CollaboratorsContext = createContext(null);

export function CollaboratorsProvider({ children }) {
  const [items, setItems] = useState(initialCollaborators);
  const [isLoading, setIsLoading] = useState(false);
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
        if (!getAuthToken()) {
          const localRecord = {
            id: Date.now(),
            name: `${payload.nome} ${payload.sobrenome}`.trim(),
            role: payload.cargo,
            projects: '',
            email: payload.email,
          };
          setItems((list) => [localRecord, ...list]);
          return localRecord;
        }

        const created = await createCollaborator(payload);
        const mapped = mapCollaboratorResponse(created);
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: (id, patch) =>
        setItems((list) =>
          list.map((item) =>
            item.id === id
              ? { ...item, ...(typeof patch === 'function' ? patch(item) : patch) }
              : item,
          ),
        ),
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
