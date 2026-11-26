import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import * as clientApi from '@/shared/api/client';
import { getAuthToken } from '@/shared/api/auth';

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
      const records = await clientApi.listClients();
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

  const refreshClient = useCallback(async (clienteId) => {
    const fresh = await clientApi.getClient(clienteId);
    setItems((list) => list.map((item) => (item.id === Number(clienteId) ? fresh : item)));
    return fresh;
  }, []);

  const value = useMemo(
    () => ({
      items,
      isLoading,
      error,
      reload,
      add: async (payload) => {
        const created = await clientApi.createClient(payload);
        setItems((list) => [created, ...list]);
        return created;
      },
      update: async (id, payload) => {
        const updated = await clientApi.updateClient(id, payload);
        setItems((list) => list.map((item) => (item.id === Number(id) ? updated : item)));
        return updated;
      },
      remove: async (id) => {
        await clientApi.deleteClient(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
      // Relacionamentos V8: representantes legais (1:N com cliente_pj)
      listRepresentatives: async (clienteId) => clientApi.listClientRepresentatives(clienteId),
      addRepresentative: async (clienteId, payload) => {
        const created = await clientApi.createClientRepresentative(clienteId, payload);
        await refreshClient(clienteId);
        return created;
      },
      updateRepresentative: async (clienteId, representanteId, payload) => {
        const updated = await clientApi.updateClientRepresentative(clienteId, representanteId, payload);
        await refreshClient(clienteId);
        return updated;
      },
      removeRepresentative: async (clienteId, representanteId) => {
        await clientApi.deleteClientRepresentative(clienteId, representanteId);
        await refreshClient(clienteId);
      },
      // Relacionamentos V8: endereços do cliente (endereco_cliente)
      listAddresses: async (clienteId) => clientApi.listClientAddresses(clienteId),
      addAddress: async (clienteId, payload) => {
        const created = await clientApi.createClientAddress(clienteId, payload);
        await refreshClient(clienteId);
        return created;
      },
      updateAddress: async (clienteId, enderecoId, payload) => {
        const updated = await clientApi.updateClientAddress(clienteId, enderecoId, payload);
        await refreshClient(clienteId);
        return updated;
      },
      removeAddress: async (clienteId, enderecoId) => {
        await clientApi.deleteClientAddress(clienteId, enderecoId);
        await refreshClient(clienteId);
      },
    }),
    [error, isLoading, items, refreshClient, reload],
  );

  return <ClientsContext.Provider value={value}>{children}</ClientsContext.Provider>;
}

export function useClients() {
  const value = useContext(ClientsContext);
  if (!value) throw new Error('ClientsProvider: Provider ausente');
  return value;
}
