import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import {
  createTask,
  listTaskCategories,
  listTaskPhases,
  listTasks,
} from '@/shared/api/task';
import { listProjects } from '@/shared/api/project';
import { initialTasks } from './mock';

const TasksContext = createContext(null);

export function TasksProvider({ children }) {
  const [items, setItems] = useState(initialTasks);
  const [categories, setCategories] = useState([]);
  const [phases, setPhases] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) return;

    setIsLoading(true);
    setError('');
    try {
      const [taskList, categoryList, phaseList] = await Promise.all([
        listTasks(),
        listTaskCategories(),
        listTaskPhases(),
      ]);
      const projectList = await listProjects();

      const categoriesMap = new Map(categoryList.map((item) => [item.id, item.nome]));
      const phasesMap = new Map(phaseList.map((item) => [item.id, item.nome]));
      const projectCodeMap = new Map(projectList.map((item) => [item.id, item.codigo]));

      setCategories(categoryList);
      setPhases(phaseList);
      setItems(taskList.map((task) => mapTaskResponse(task, categoriesMap, phasesMap, projectCodeMap)));
    } catch {
      setError('Nao foi possivel carregar as tarefas.');
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
      categories,
      phases,
      isLoading,
      error,
      reload,
      add: async (payload) => {
        if (!getAuthToken()) {
          const localRecord = mapLocalTaskPayload(payload);
          setItems((list) => [localRecord, ...list]);
          return localRecord;
        }

        const categoriesMap = new Map(categories.map((item) => [item.id, item.nome]));
        const phasesMap = new Map(phases.map((item) => [item.id, item.nome]));
        const created = await createTask(payload);
        const mapped = {
          ...mapTaskResponse(created, categoriesMap, phasesMap, new Map()),
          project: payload.project,
        };
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
    [categories, error, isLoading, items, phases, reload],
  );

  return <TasksContext.Provider value={value}>{children}</TasksContext.Provider>;
}

export function useTasks() {
  const value = useContext(TasksContext);
  if (!value) throw new Error('TasksProvider: Provider ausente');
  return value;
}

function mapTaskResponse(task, categoriesMap, phasesMap, projectCodeMap) {
  return {
    id: task.id,
    name: task.nome,
    projectId: task.projetoId,
    clientId: task.clienteId,
    categoryId: task.categoriaId,
    stageId: task.fkTarefaFase,
    project: projectCodeMap.get(task.projetoId) || `Projeto #${task.projetoId}`,
    member: '',
    start: onlyDate(task.dataInicioPrev),
    end: onlyDate(task.dataFimPrev),
    status: task.status,
    priority: task.prioridade,
    category: categoriesMap.get(task.categoriaId) || `Categoria ${task.categoriaId}`,
    stage: phasesMap.get(task.fkTarefaFase) || `Fase ${task.fkTarefaFase}`,
    note: task.observacao || '',
    done: task.status === 'Concluído',
    checklist: [],
    timeLogs: [],
  };
}

function mapLocalTaskPayload(payload) {
  return {
    id: Date.now(),
    name: payload.nome,
    projectId: payload.projetoId,
    clientId: payload.clienteId,
    categoryId: payload.categoriaId,
    stageId: payload.fkTarefaFase,
    project: payload.project,
    member: payload.member,
    start: onlyDate(payload.dataInicioPrev),
    end: onlyDate(payload.dataFimPrev),
    status: payload.status,
    priority: payload.prioridade,
    category: payload.category,
    stage: payload.stage,
    note: payload.observacao || '',
    done: payload.status === 'Concluído',
    checklist: payload.checklist || [],
    timeLogs: [],
  };
}

function onlyDate(dateTime) {
  return dateTime ? String(dateTime).slice(0, 10) : '';
}
