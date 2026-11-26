import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import * as supplierApi from '@/shared/api/supplier';

const Context = createContext(null);

export function SuppliersProvider({ children }) {
  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');

  const reloadCategories = useCallback(async () => {
    if (!getAuthToken()) return [];
    try {
      const records = await supplierApi.listSupplierCategories();
      setCategories(records);
      return records;
    } catch {
      return [];
    }
  }, []);

  const reload = useCallback(async () => {
    if (!getAuthToken()) {
      setIsLoading(false);
      setError('');
      return;
    }
    setIsLoading(true);
    try {
      const [supplierList, categoryList] = await Promise.all([
        supplierApi.listSuppliers(),
        supplierApi.listSupplierCategories().catch(() => []),
      ]);
      setItems(supplierList.map(mapSupplierResponse));
      setCategories(categoryList);
      setError('');
    } catch {
      setError('Não foi possível carregar os fornecedores.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    reload();
  }, [reload]);

  const refreshSupplier = useCallback(async (id) => {
    const fresh = mapSupplierResponse(await supplierApi.getSupplier(id));
    setItems((list) => list.map((item) => (item.id === Number(id) ? fresh : item)));
    return fresh;
  }, []);

  const value = useMemo(
    () => ({
      items,
      categories,
      isLoading,
      error,
      reload,
      reloadCategories,
      add: async (payload) => {
        const created = mapSupplierResponse(await supplierApi.createSupplier(payload));
        setItems((list) => [created, ...list]);
        reloadCategories();
        return created;
      },
      update: async (id, payload) => {
        const updated = mapSupplierResponse(await supplierApi.updateSupplier(id, payload));
        setItems((list) => list.map((item) => (item.id === Number(id) ? updated : item)));
        reloadCategories();
        return updated;
      },
      remove: async (id) => {
        await supplierApi.deleteSupplier(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
      // Categorias V8 (fornecedor_categoria)
      addCategory: async (payload) => {
        const created = await supplierApi.createSupplierCategory(payload);
        setCategories((list) => [...list, created].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR')));
        return created;
      },
      updateCategory: async (id, payload) => {
        const updated = await supplierApi.updateSupplierCategory(id, payload);
        setCategories((list) =>
          list.map((c) => (c.id === Number(id) ? updated : c)).sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR')),
        );
        return updated;
      },
      removeCategory: async (id) => {
        await supplierApi.deleteSupplierCategory(id);
        setCategories((list) => list.filter((c) => c.id !== Number(id)));
      },
      // Contatos V8 (fornecedor_contato)
      listContacts: async (fornecedorId) => supplierApi.listSupplierContacts(fornecedorId),
      addContact: async (fornecedorId, payload) => {
        const created = await supplierApi.createSupplierContact(fornecedorId, payload);
        await refreshSupplier(fornecedorId);
        return created;
      },
      updateContact: async (fornecedorId, contatoId, payload) => {
        const updated = await supplierApi.updateSupplierContact(fornecedorId, contatoId, payload);
        await refreshSupplier(fornecedorId);
        return updated;
      },
      removeContact: async (fornecedorId, contatoId) => {
        await supplierApi.deleteSupplierContact(fornecedorId, contatoId);
        await refreshSupplier(fornecedorId);
      },
      // Projetos V8 (fornecedor_projeto)
      listProjects: async (fornecedorId) => supplierApi.listSupplierProjects(fornecedorId),
      linkProject: async (fornecedorId, payload) => {
        const created = await supplierApi.linkSupplierProject(fornecedorId, payload);
        await refreshSupplier(fornecedorId);
        return created;
      },
      updateProjectLink: async (fornecedorId, projetoId, payload) => {
        const updated = await supplierApi.updateSupplierProject(fornecedorId, projetoId, payload);
        await refreshSupplier(fornecedorId);
        return updated;
      },
      unlinkProject: async (fornecedorId, projetoId) => {
        await supplierApi.unlinkSupplierProject(fornecedorId, projetoId);
        await refreshSupplier(fornecedorId);
      },
    }),
    [categories, error, isLoading, items, refreshSupplier, reload, reloadCategories],
  );

  return <Context.Provider value={value}>{children}</Context.Provider>;
}

export function useSuppliers() {
  const store = useContext(Context);
  if (!store) throw new Error('SuppliersProvider: Provider ausente');
  return store;
}

function mapSupplierResponse(item) {
  return {
    ...item,
    name: item.name ?? item.nome ?? '',
    nome: item.nome ?? item.name ?? '',
    category: item.category ?? item.categorias?.[0]?.nome ?? '',
    city: item.city ?? item.cidade ?? '',
    cidade: item.cidade ?? item.city ?? '',
    note: item.note ?? item.observacao ?? '',
    observacao: item.observacao ?? item.note ?? '',
    origem: item.origem ?? '',
    estado: item.estado ?? '',
    jaTrabalhei: Boolean(item.jaTrabalhei),
    projects: item.projects ?? '',
    categorias: Array.isArray(item.categorias) ? item.categorias : [],
    contatos: Array.isArray(item.contatos) ? item.contatos : [],
    projetosVinculados: Array.isArray(item.projetosVinculados) ? item.projetosVinculados : [],
  };
}
