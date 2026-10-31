import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import { createProject, listProjects } from '@/shared/api/project';
import { initialProjects } from './mock';

const ProjectsContext = createContext(null);

export function ProjectsProvider({ children }) {
  const [items, setItems] = useState(initialProjects);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) return;

    setIsLoading(true);
    setError('');
    try {
      const records = await listProjects();
      setItems(records.map(mapProjectResponse));
    } catch {
      setError('Nao foi possivel carregar os projetos.');
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
          const localRecord = mapLocalProjectPayload(payload);
          setItems((list) => [localRecord, ...list]);
          return localRecord;
        }

        const created = await createProject(payload);
        const mapped = mapProjectResponse(created);
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

  return <ProjectsContext.Provider value={value}>{children}</ProjectsContext.Provider>;
}

export function useProjects() {
  const value = useContext(ProjectsContext);
  if (!value) throw new Error('ProjectsProvider: Provider ausente');
  return value;
}

function mapProjectResponse(item) {
  const endedAt = item.dataFim ? new Date(item.dataFim) : null;
  const isDone = endedAt && endedAt < new Date();

  return {
    id: item.id,
    name: item.nome,
    code: item.codigo,
    status: isDone ? 'Concluído' : 'Em andamento',
    clientId: item.clienteId,
    stageId: item.fkTarefaFase,
    stage: item.fkTarefaFase ? `Fase ${item.fkTarefaFase}` : '—',
    address: item.condominio || item.unidadeNumero || '—',
    note: item.observacao || '',
    details: {
      propertyType: item.tipoImovel || '',
      area: item.areaM2 ?? '',
      condo: item.condominio || '',
      registry: item.matricula || '',
    },
  };
}

function mapLocalProjectPayload(payload) {
  return {
    id: Date.now(),
    name: payload.nome,
    code: payload.codigo,
    status: payload.status || 'Em andamento',
    clientId: payload.clienteId,
    client: payload.clientName,
    stageId: payload.fkTarefaFase,
    stage: payload.stage || (payload.fkTarefaFase ? `Fase ${payload.fkTarefaFase}` : '—'),
    address: payload.address || payload.condominio || payload.unidadeNumero || '—',
    note: payload.observacao || '',
    details: payload.details || {
      propertyType: payload.tipoImovel || '',
      area: payload.areaM2 ?? '',
      condo: payload.condominio || '',
      registry: payload.matricula || '',
    },
  };
}
