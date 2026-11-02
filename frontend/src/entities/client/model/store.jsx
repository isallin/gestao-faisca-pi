import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { createClient, deleteClient, listClients, updateClient } from '@/shared/api/client';
import { getAuthToken } from '@/shared/api/auth';
// import { initialClients } from './mock';

const ClientsContext = createContext(null);

export function ClientsProvider({ children }) {
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
      const records = await listClients();
      setItems(records);
    } catch {
      setError('Não foi possível carregar os clientes. Confira a sessão e tente novamente.');
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
        const created = await createClient(payload);
        setItems((list) => [created, ...list]);
        return created;
      },
      update: async (id, payload) => {
        const updated = await updateClient(id, payload);
        setItems((list) => list.map((item) => item.id === id ? updated : item));
        return updated;
      },
      remove: async (id) => {
        await deleteClient(id);
        setItems((list) => list.filter((item) => item.id !== id));
      },
    }),
    [error, isLoading, items, reload],
  );

  return <ClientsContext.Provider value={value}>{children}</ClientsContext.Provider>;
}

export function useClients() {
  const value = useContext(ClientsContext);
  if (!value) throw new Error('ClientsProvider: Provider ausente');
  return value;
}
