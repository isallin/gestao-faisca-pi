import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import {
  createTask,
  deleteTask,
  listTaskCategories,
  listTaskPhases,
  listTasks,
  updateTask,
  updateTaskStatus,
} from '@/shared/api/task';
import { listProjects } from '@/shared/api/project';

const TasksContext = createContext(null);

export function TasksProvider({ children }) {
  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [phases, setPhases] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) return;

    setIsLoading(true);
    setError('');
    try {
      const categoryList = await listTaskCategories();
      setCategories(categoryList);
      const [taskList, phaseList, projectList] = await Promise.all([
        listTasks(),
        listTaskPhases(),
        listProjects(),
      ]);

      const categoriesMap = new Map(categoryList.map((item) => [item.id, item.nome]));
      const phasesMap = new Map(phaseList.map((item) => [item.id, item.nome]));
      const projectCodeMap = new Map(projectList.map((item) => [item.id, item.codigo]));

      setPhases(phaseList);
      setItems(taskList.map((task) => mapTaskResponse(task, categoriesMap, phasesMap, projectCodeMap)));
    } catch (cause) {
      setError(`Nao foi possivel carregar dados de tarefas: ${cause.message}`);
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
      update: async (id, patch) => {
        const current = items.find((item) => item.id === id);
        if (!current) throw new Error('Tarefa não encontrada');
        const changes = typeof patch === 'function' ? patch(current) : patch;
        if (changes.projetoId && changes.categoriaId && changes.fkTarefaFase) {
          const updated = await updateTask(id, changes);
          const mapped = mapTaskResponse(updated, new Map(categories.map((c) => [c.id, c.nome])), new Map(phases.map((p) => [p.id, p.nome])), new Map());
          setItems((list) => list.map((item) => item.id === id ? { ...item, ...mapped, project: changes.project } : item));
          return mapped;
        }
        if (changes.status && changes.status !== current.status) await updateTaskStatus(id, changes.status);
        setItems((list) => list.map((item) => item.id === id ? { ...item, ...changes } : item));
      },
      remove: async (id) => {
        await deleteTask(id);
        setItems((list) => list.filter((item) => item.id !== id));
      },
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


function onlyDate(dateTime) {
  return dateTime ? String(dateTime).slice(0, 10) : '';
}
