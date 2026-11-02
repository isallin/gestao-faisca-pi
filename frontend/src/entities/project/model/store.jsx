import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import { listPhases } from '@/shared/api/phase';
import { createProject, deleteProject, listProjects, updateProject } from '@/shared/api/project';
// import { initialProjects } from './mock';

const ProjectsContext = createContext(null);

export function ProjectsProvider({ children }) {
  const [items, setItems] = useState([]);
  const [phases, setPhases] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
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

        const created = await createProject(payload);
        const mapped = mapProjectResponse(created, phaseMap);
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: async (id, payload) => {
        const updated = await updateProject(id, payload);
        const mapped = mapProjectResponse(updated, new Map(phases.map((p) => [p.id, p.nome])));
        setItems((list) => list.map((item) => item.id === Number(id) ? mapped : item));
        return mapped;
      },
      remove: async (id) => {
        await deleteProject(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
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
    address: item.cidade ? `${item.bairro ? `${item.bairro}, ` : ''}${item.cidade}-${item.estado || ''}` : (item.condominio || item.unidadeNumero || '—'),
    note: item.observacao || '',
    start: item.dataInicio?.slice(0, 10) || '',
    end: item.dataFim?.slice(0, 10) || '',
    details: {
      propertyType: item.tipoImovel || '',
      area: item.areaM2 ?? '',
      condo: item.condominio || '',
      registry: item.matricula || '',
      number: item.unidadeNumero || '',
      zip: item.cep || '', street: item.logradouro || '', complement: item.complemento || '',
      neighborhood: item.bairro || '', city: item.cidade || '', state: item.estado || '', country: item.pais || 'Brasil',
    },
  };
}
