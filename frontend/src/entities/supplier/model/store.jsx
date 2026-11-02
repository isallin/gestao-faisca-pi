import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import { listSuppliers, createSupplier, updateSupplier, deleteSupplier } from '@/shared/api/supplier';

const Context = createContext(null);

export function SuppliersProvider({ children }) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');
  const reload = useCallback(async () => {
    if (!getAuthToken()) { setIsLoading(false); return; }
    setIsLoading(true);
    try { setItems(await listSuppliers()); setError(''); }
    catch { setError('Não foi possível carregar os fornecedores.'); }
    finally { setIsLoading(false); }
  }, []);
  useEffect(() => { reload(); }, [reload]);

  const value = useMemo(() => ({
    items, isLoading, error, reload,
    add: async (payload) => {
      const created = await createSupplier(payload);
      setItems((list) => [created, ...list]);
      return created;
    },
    update: async (id, payload) => {
      const updated = await updateSupplier(id, payload);
      setItems((list) => list.map((item) => item.id === Number(id) ? updated : item));
      return updated;
    },
    remove: async (id) => {
      await deleteSupplier(id);
      setItems((list) => list.filter((item) => item.id !== Number(id)));
    },
  }), [items, isLoading, error, reload]);
  return <Context.Provider value={value}>{children}</Context.Provider>;
}

export function useSuppliers() {
  const store = useContext(Context);
  if (!store) throw new Error('SuppliersProvider: Provider ausente');
  return store;
}
