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
import { createRecord, deleteRecord, listRecords, updateRecord } from '@/shared/api/record';
import { listCollaborators } from '@/shared/api/collaborator';

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
      const [taskList, phaseList, projectList, records, collaborators] = await Promise.all([
        listTasks(),
        listTaskPhases(),
        listProjects(),
        listRecords(),
        listCollaborators(),
      ]);

      const categoriesMap = new Map(categoryList.map((item) => [item.id, item.nome]));
      const phasesMap = new Map(phaseList.map((item) => [item.id, item.nome]));
      const projectCodeMap = new Map(projectList.map((item) => [item.id, item.codigo]));
      const memberMap = new Map(collaborators.map((user) => [user.id, `${user.nome} ${user.sobrenome}`]));

      setPhases(phaseList);
      setItems(taskList.map((task) => ({
        ...mapTaskResponse(task, categoriesMap, phasesMap, projectCodeMap, memberMap),
        timeLogs: records.filter((record) => record.tarefaId === task.id).map(mapRecord),
      })));
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
        const mapped = mapTaskResponse(created, categoriesMap, phasesMap, new Map([[created.projetoId, payload.project]]), new Map([[created.colaboradorId, payload.member]]));
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: async (id, patch) => {
        const current = items.find((item) => item.id === Number(id));
        if (!current) throw new Error('Tarefa não encontrada');
        const changes = typeof patch === 'function' ? patch(current) : patch;
        if (changes.projetoId || changes.checklist) {
          const payload = changes.projetoId ? changes : {
            projetoId: current.projectId, categoriaId: current.categoryId, fkTarefaFase: current.stageId,
            nome: current.name, prioridade: current.priority, status: current.status,
            dataInicioPrev: `${current.start}T00:00:00`, dataFimPrev: `${current.end}T00:00:00`,
            observacao: current.note, colaboradorId: current.memberId,
            checklist: changes.checklist,
          };
          const updated = await updateTask(id, payload);
          const mapped = mapTaskResponse(updated, new Map(categories.map((c) => [c.id, c.nome])), new Map(phases.map((p) => [p.id, p.nome])), new Map([[updated.projetoId, changes.project || current.project]]), new Map([[updated.colaboradorId, changes.member || current.member]]));
          setItems((list) => list.map((item) => item.id === Number(id) ? { ...item, ...mapped, timeLogs: item.timeLogs } : item));
          return mapped;
        }
        if (changes.status && changes.status !== current.status) await updateTaskStatus(id, changes.status);
        setItems((list) => list.map((item) => item.id === Number(id) ? { ...item, ...changes } : item));
      },
      remove: async (id) => {
        await deleteTask(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
      addLog: async (payload) => {
        const saved = mapRecord(await createRecord(payload));
        setItems((list) => list.map((task) => task.id === payload.tarefaId
          ? { ...task, timeLogs: [...task.timeLogs, saved] } : task));
        return saved;
      },
      editLog: async (id, payload) => {
        const saved = mapRecord(await updateRecord(id, payload));
        setItems((list) => list.map((task) => ({ ...task,
          timeLogs: task.timeLogs.filter((log) => log.id !== id).concat(task.id === payload.tarefaId ? [saved] : []),
        })));
      },
      removeLog: async (id) => {
        await deleteRecord(id);
        setItems((list) => list.map((task) => ({ ...task, timeLogs: task.timeLogs.filter((log) => log.id !== id) })));
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

function mapTaskResponse(task, categoriesMap, phasesMap, projectCodeMap, memberMap = new Map()) {
  return {
    id: task.id,
    name: task.nome,
    projectId: task.projetoId,
    clientId: task.clienteId,
    categoryId: task.categoriaId,
    stageId: task.fkTarefaFase,
    project: projectCodeMap.get(task.projetoId) || `Projeto #${task.projetoId}`,
    memberId: task.colaboradorId,
    member: memberMap.get(task.colaboradorId) || '',
    start: onlyDate(task.dataInicioPrev),
    end: onlyDate(task.dataFimPrev),
    status: task.status,
    priority: task.prioridade,
    category: categoriesMap.get(task.categoriaId) || `Categoria ${task.categoriaId}`,
    stage: phasesMap.get(task.fkTarefaFase) || `Fase ${task.fkTarefaFase}`,
    note: task.observacao || '',
    done: task.status === 'Concluído',
    checklist: task.checklist || [],
    timeLogs: [],
  };
}


function onlyDate(dateTime) {
  return dateTime ? String(dateTime).slice(0, 10) : '';
}

function mapRecord(record) {
  const hours = (new Date(record.fim) - new Date(record.inicio)) / 3600000;
  return { id: record.id, date: onlyDate(record.inicio), start: record.inicio, end: record.fim,
    hours, note: record.descricao || '' };
}
