import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { createClient, listClients } from '@/shared/api/client';
import { getAuthToken } from '@/shared/api/auth';
import { initialClients } from './mock';

const ClientsContext = createContext(null);

export function ClientsProvider({ children }) {
  const [items, setItems] = useState(initialClients);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const buildLocalClient = (payload) => {
    const isPf = payload.tipo === 'PF';
    return {
      id: Date.now(),
      name: isPf ? payload.nome : payload.nomeFantasia,
      type: isPf ? 'Pessoa Física' : 'Pessoa Jurídica',
      projects: '—',
      address: `${payload.logradouro}, ${payload.numero} — ${payload.bairro}, ${payload.cidade}-${String(payload.estado ?? '').toUpperCase()}`,
      phone: payload.telefone,
      email: payload.email,
      note: payload.observacao ?? '',
    };
  };

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
      setError('Nao foi possivel carregar os clientes. Confira a sessao e tente novamente.');
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
          const created = buildLocalClient(payload);
          setItems((list) => [created, ...list]);
          return created;
        }

        const created = await createClient(payload);
        setItems((list) => [created, ...list]);
        return created;
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

  return <ClientsContext.Provider value={value}>{children}</ClientsContext.Provider>;
}

export function useClients() {
  const value = useContext(ClientsContext);
  if (!value) throw new Error('ClientsProvider: Provider ausente');
  return value;
}
