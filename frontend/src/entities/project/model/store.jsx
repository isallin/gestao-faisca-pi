import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import * as phaseApi from '@/shared/api/phase';
import * as projectApi from '@/shared/api/project';

const ProjectsContext = createContext(null);

export function ProjectsProvider({ children }) {
  const [items, setItems] = useState([]);
  const [phases, setPhases] = useState([]);
  const [phaseError, setPhaseError] = useState('');
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');

  const reloadPhases = useCallback(async () => {
    if (!getAuthToken()) return [];
    setPhaseError('');
    try {
      const records = await phaseApi.listPhases();
      setPhases(records);
      return records;
    } catch {
      setPhaseError('Não foi possível carregar as fases. Tente novamente.');
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
    setError('');
    const phaseRecords = await reloadPhases();
    try {
      const records = await projectApi.listProjects();
      const phaseMap = new Map(phaseRecords.map((phase) => [phase.id, phase.nome]));

      setItems(records.map((record) => mapProjectResponse(record, phaseMap)));
    } catch {
      setError('Não foi possível carregar os projetos.');
    } finally {
      setIsLoading(false);
    }
  }, [reloadPhases]);

  useEffect(() => {
    reload();
  }, [reload]);

  const value = useMemo(
    () => ({
      items,
      phases,
      phaseError,
      reloadPhases,
      isLoading,
      error,
      reload,
      add: async (payload) => {
        const phaseMap = new Map(phases.map((phase) => [phase.id, phase.nome]));
        const created = await projectApi.createProject(payload);
        const mapped = mapProjectResponse(created, phaseMap);
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: async (id, payload) => {
        const updated = await projectApi.updateProject(id, payload);
        const mapped = mapProjectResponse(updated, new Map(phases.map((p) => [p.id, p.nome])));
        setItems((list) => list.map((item) => (item.id === Number(id) ? mapped : item)));
        return mapped;
      },
      remove: async (id) => {
        await projectApi.deleteProject(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
      // CRUD de Fases (V8)
      addPhase: async (payload) => {
        const created = await phaseApi.createPhase(payload);
        setPhases((list) => [...list, created].sort((a, b) => (a.ordem ?? 0) - (b.ordem ?? 0)));
        return created;
      },
      updatePhase: async (id, payload) => {
        const updated = await phaseApi.updatePhase(id, payload);
        setPhases((list) =>
          list.map((p) => (p.id === Number(id) ? updated : p)).sort((a, b) => (a.ordem ?? 0) - (b.ordem ?? 0)),
        );
        return updated;
      },
      removePhase: async (id) => {
        await phaseApi.deletePhase(id);
        setPhases((list) => list.filter((p) => p.id !== Number(id)));
      },
      // Relacionamentos V8 do Projeto (endereco_projeto, fornecedor_projeto, tarefa_fase)
      getAddress: async (projectId) => projectApi.getProjectAddress(projectId),
      saveAddress: async (projectId, payload) => {
        const saved = await projectApi.saveProjectAddress(projectId, payload);
        const fresh = await projectApi.getProject(projectId);
        const phaseMap = new Map(phases.map((p) => [p.id, p.nome]));
        const mapped = mapProjectResponse(fresh, phaseMap);
        setItems((list) => list.map((item) => (item.id === Number(projectId) ? mapped : item)));
        return saved;
      },
      removeAddress: async (projectId) => {
        await projectApi.deleteProjectAddress(projectId);
        const fresh = await projectApi.getProject(projectId);
        const phaseMap = new Map(phases.map((p) => [p.id, p.nome]));
        const mapped = mapProjectResponse(fresh, phaseMap);
        setItems((list) => list.map((item) => (item.id === Number(projectId) ? mapped : item)));
      },
      listSuppliers: async (projectId) => projectApi.listProjectSuppliers(projectId),
      listPhaseLinks: async (projectId) => projectApi.listProjectPhases(projectId),
    }),
    [error, isLoading, items, phases, phaseError, reload, reloadPhases],
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
  const ep = item.endereco ?? null;
  const city = item.cidade ?? ep?.cidade ?? '';
  const state = item.estado ?? ep?.estado ?? '';
  const neighborhood = item.bairro ?? ep?.bairro ?? '';

  return {
    id: item.id,
    name: item.nome,
    code: item.codigo,
    status: isDone ? 'Concluído' : 'Em andamento',
    clientId: item.clienteId,
    stageId: item.fkTarefaFase,
    stage: phaseMap.get(item.fkTarefaFase) || (item.fkTarefaFase ? `Fase ${item.fkTarefaFase}` : '—'),
    address: city
      ? `${neighborhood ? `${neighborhood}, ` : ''}${city}-${state}`
      : (item.condominio || item.unidadeNumero || '—'),
    note: item.observacao || '',
    start: item.dataInicio?.slice(0, 10) || '',
    end: item.dataFim?.slice(0, 10) || '',
    addressEntity: ep,
    suppliers: Array.isArray(item.fornecedores) ? item.fornecedores : [],
    details: {
      ownerRelation: item.relacaoProprietario || '',
      propertyType: item.tipoImovel || '',
      area: item.areaM2 ?? '',
      condo: item.condominio || '',
      registry: item.matricula || '',
      number: item.numero || item.unidadeNumero || ep?.numero || '',
      unitNumber: item.unidadeNumero || '',
      zip: item.cep || ep?.cep || '',
      street: item.logradouro || ep?.logradouro || '',
      complement: item.complemento || ep?.complemento || '',
      neighborhood,
      city,
      state,
      country: item.pais || ep?.pais || 'Brasil',
    },
  };
}
