import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import { listPhases } from '@/shared/api/phase';
import { createProject, listProjects } from '@/shared/api/project';
// import { initialProjects } from './mock';

const FALLBACK_PHASES = [
  { id: 1, nome: 'Pré-contrato', ordem: 1 },
  { id: 2, nome: 'Levantamento', ordem: 2 },
  { id: 3, nome: 'Estudo preliminar', ordem: 3 },
  { id: 4, nome: 'Estudo de anteprojeto', ordem: 4 },
  { id: 5, nome: 'Projeto legal', ordem: 5 },
  { id: 6, nome: 'Pré-executivo', ordem: 6 },
  { id: 7, nome: 'Executivo', ordem: 7 },
  { id: 8, nome: 'Pré-obra', ordem: 8 },
  { id: 9, nome: 'Obra', ordem: 9 },
];

const ProjectsContext = createContext(null);

export function ProjectsProvider({ children }) {
  const [items, setItems] = useState([]);
  const [phases, setPhases] = useState(FALLBACK_PHASES);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) return;

    setIsLoading(true);
    setError('');
    try {
      const [records, phaseRecords] = await Promise.all([listProjects(), listPhases()]);
      const phaseMap = new Map(phaseRecords.map((phase) => [phase.id, phase.nome]));

      setPhases(phaseRecords);
      setItems(records.map((record) => mapProjectResponse(record, phaseMap)));
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
      phases,
      isLoading,
      error,
      reload,
      add: async (payload) => {
        const phaseMap = new Map(phases.map((phase) => [phase.id, phase.nome]));

        if (!getAuthToken()) {
          const localRecord = mapLocalProjectPayload(payload, phaseMap);
          setItems((list) => [localRecord, ...list]);
          return localRecord;
        }

        const created = await createProject(payload);
        const mapped = mapProjectResponse(created, phaseMap);
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
    [error, isLoading, items, phases, reload],
  );

  return <ProjectsContext.Provider value={value}>{children}</ProjectsContext.Provider>;
}

export function useProjects() {
  const value = useContext(ProjectsContext);
  if (!value) throw new Error('ProjectsProvider: Provider ausente');
  return value;
}

function mapProjectResponse(item, phaseMap) {
  const endedAt = item.dataFim ? new Date(item.dataFim) : null;
  const isDone = endedAt && endedAt < new Date();

  return {
    id: item.id,
    name: item.nome,
    code: item.codigo,
    status: isDone ? 'Concluído' : 'Em andamento',
    clientId: item.clienteId,
    stageId: item.fkTarefaFase,
    stage: phaseMap.get(item.fkTarefaFase) || (item.fkTarefaFase ? `Fase ${item.fkTarefaFase}` : '—'),
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

function mapLocalProjectPayload(payload, phaseMap) {
  return {
    id: Date.now(),
    name: payload.nome,
    code: payload.codigo,
    status: payload.status || 'Em andamento',
    clientId: payload.clienteId,
    client: payload.clientName,
    stageId: payload.fkTarefaFase,
    stage:
      payload.stage ||
      phaseMap.get(payload.fkTarefaFase) ||
      (payload.fkTarefaFase ? `Fase ${payload.fkTarefaFase}` : '—'),
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
