import { createContext, useContext, useMemo, useState } from 'react';

/**
 * Fábrica de "stores" em memória (Context + useState) para uma coleção de itens com `id`.
 * Cada entidade cria a sua, mantendo o estado isolado por slice.
 */
export function createCollectionStore(initialItems, name) {
  const Context = createContext(null);

  function Provider({ children }) {
    const [items, setItems] = useState(initialItems);
    const value = useMemo(
      () => ({
        items,
        add: (item) => setItems((list) => [item, ...list]),
        update: (id, patch) =>
          setItems((list) =>
            list.map((item) =>
              item.id === id
                ? { ...item, ...(typeof patch === 'function' ? patch(item) : patch) }
                : item,
            ),
          ),
      }),
      [items],
    );
    return <Context.Provider value={value}>{children}</Context.Provider>;
  }

  function useCollection() {
    const value = useContext(Context);
    if (!value) throw new Error(`${name}: Provider ausente`);
    return value;
  }

  return { Provider, useCollection };
}
