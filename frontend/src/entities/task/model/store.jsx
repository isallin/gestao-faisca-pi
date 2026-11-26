import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { getAuthToken } from '@/shared/api/auth';
import * as taskApi from '@/shared/api/task';
import * as priorityApi from '@/shared/api/priority';
import * as projectApi from '@/shared/api/project';
import * as recordApi from '@/shared/api/record';
import * as collaboratorApi from '@/shared/api/collaborator';

const TasksContext = createContext(null);

export function TasksProvider({ children }) {
  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [phases, setPhases] = useState([]);
  const [priorities, setPriorities] = useState([]);
  const [isLoading, setIsLoading] = useState(Boolean(getAuthToken()));
  const [error, setError] = useState('');

  const reload = useCallback(async () => {
    if (!getAuthToken()) {
      setIsLoading(false);
      setError('');
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      const categoryList = await taskApi.listTaskCategories();
      setCategories(categoryList);
      const [taskList, phaseList, projectList, records, collaborators, priorityList] = await Promise.all([
        taskApi.listTasks(),
        taskApi.listTaskPhases(),
        projectApi.listProjects(),
        recordApi.listRecords(),
        collaboratorApi.listCollaborators(),
        priorityApi.listPriorities().catch(() => []),
      ]);

      const categoriesMap = new Map(categoryList.map((item) => [item.id, item.nome]));
      const phasesMap = new Map(phaseList.map((item) => [item.id, item.nome]));
      const prioritiesMap = new Map(priorityList.map((item) => [item.id, item.nome]));
      const projectCodeMap = new Map(projectList.map((item) => [item.id, item.codigo]));
      const memberMap = new Map(
        collaborators.map((user) => [user.id, `${user.nome ?? ''} ${user.sobrenome ?? ''}`.trim()]),
      );

      setPhases(phaseList);
      setPriorities(priorityList);
      setItems(
        taskList.map((task) => ({
          ...mapTaskResponse(task, categoriesMap, phasesMap, projectCodeMap, memberMap, prioritiesMap),
          timeLogs: records.filter((record) => record.tarefaId === task.id).map(mapRecord),
        })),
      );
    } catch (cause) {
      setError(`Não foi possível carregar dados de tarefas: ${cause.message}`);
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
      priorities,
      isLoading,
      error,
      reload,
      add: async (payload) => {
        const categoriesMap = new Map(categories.map((item) => [item.id, item.nome]));
        const phasesMap = new Map(phases.map((item) => [item.id, item.nome]));
        const prioritiesMap = new Map(priorities.map((item) => [item.id, item.nome]));
        const created = await taskApi.createTask(payload);
        const mapped = mapTaskResponse(
          created,
          categoriesMap,
          phasesMap,
          new Map([[created.projetoId, payload.project]]),
          new Map([[created.colaboradorId, payload.member]]),
          prioritiesMap,
        );
        setItems((list) => [mapped, ...list]);
        return mapped;
      },
      update: async (id, patch) => {
        const current = items.find((item) => item.id === Number(id));
        if (!current) throw new Error('Tarefa não encontrada');
        const changes = typeof patch === 'function' ? patch(current) : patch;
        if (changes.projetoId || changes.checklist || changes.subtarefas) {
          const payload = changes.projetoId
            ? changes
            : {
                projetoId: current.projectId,
                categoriaId: current.categoryId,
                fkTarefaFase: current.stageId,
                nome: current.name,
                prioridade: current.priority,
                prioridadeId: current.priorityId ?? null,
                status: current.status,
                dataInicioPrev: `${current.start}T00:00:00`,
                dataFimPrev: `${current.end}T00:00:00`,
                observacao: current.note,
                colaboradorId: current.memberId,
                colaboradoresIds: current.collaboratorIds?.length ? current.collaboratorIds : undefined,
                checklist: changes.checklist,
                subtarefas: changes.subtarefas,
              };
          const updated = await taskApi.updateTask(id, payload);
          const mapped = mapTaskResponse(
            updated,
            new Map(categories.map((c) => [c.id, c.nome])),
            new Map(phases.map((p) => [p.id, p.nome])),
            new Map([[updated.projetoId, changes.project || current.project]]),
            new Map([[updated.colaboradorId, changes.member || current.member]]),
            new Map(priorities.map((p) => [p.id, p.nome])),
          );
          setItems((list) =>
            list.map((item) => (item.id === Number(id) ? { ...item, ...mapped, timeLogs: item.timeLogs } : item)),
          );
          return mapped;
        }
        if (changes.status && changes.status !== current.status) {
          await taskApi.updateTaskStatus(id, changes.status);
        }
        setItems((list) => list.map((item) => (item.id === Number(id) ? { ...item, ...changes } : item)));
      },
      remove: async (id) => {
        await taskApi.deleteTask(id);
        setItems((list) => list.filter((item) => item.id !== Number(id)));
      },
      // Registros de horas (registro_horas V8)
      addLog: async (payload) => {
        const saved = mapRecord(await recordApi.createRecord(payload));
        setItems((list) =>
          list.map((task) => (task.id === payload.tarefaId ? { ...task, timeLogs: [...task.timeLogs, saved] } : task)),
        );
        return saved;
      },
      editLog: async (id, payload) => {
        const saved = mapRecord(await recordApi.updateRecord(id, payload));
        setItems((list) =>
          list.map((task) => ({
            ...task,
            timeLogs: task.timeLogs
              .filter((log) => log.id !== id)
              .concat(task.id === payload.tarefaId ? [saved] : []),
          })),
        );
      },
      removeLog: async (id) => {
        await recordApi.deleteRecord(id);
        setItems((list) =>
          list.map((task) => ({ ...task, timeLogs: task.timeLogs.filter((log) => log.id !== id) })),
        );
      },
      // Subtarefas (subtarefa V8)
      addSubtask: async (tarefaId, payload) => {
        const created = await taskApi.createSubtask(tarefaId, payload);
        setItems((list) =>
          list.map((task) => {
            if (task.id !== Number(tarefaId)) return task;
            const subtasks = [...(task.subtasks ?? []), created];
            const checklist = subtasks.map((s) => ({ id: s.id, text: s.nome, done: Boolean(s.check) }));
            return { ...task, subtasks, checklist };
          }),
        );
        return created;
      },
      toggleSubtask: async (tarefaId, subtarefaId, check) => {
        const updated = await taskApi.toggleSubtaskCheck(tarefaId, subtarefaId, check);
        setItems((list) =>
          list.map((task) => {
            if (task.id !== Number(tarefaId)) return task;
            const subtasks = (task.subtasks ?? []).map((s) => (s.id === Number(subtarefaId) ? updated : s));
            const checklist = subtasks.map((s) => ({ id: s.id, text: s.nome, done: Boolean(s.check) }));
            return { ...task, subtasks, checklist };
          }),
        );
        return updated;
      },
      removeSubtask: async (tarefaId, subtarefaId) => {
        await taskApi.deleteSubtask(tarefaId, subtarefaId);
        setItems((list) =>
          list.map((task) => {
            if (task.id !== Number(tarefaId)) return task;
            const subtasks = (task.subtasks ?? []).filter((s) => s.id !== Number(subtarefaId));
            const checklist = subtasks.map((s) => ({ id: s.id, text: s.nome, done: Boolean(s.check) }));
            return { ...task, subtasks, checklist };
          }),
        );
      },
      // Colaboradores da tarefa (colaborador_tarefa V8)
      assignCollaborator: async (tarefaId, payload) => {
        const created = await taskApi.assignTaskCollaborator(tarefaId, payload);
        setItems((list) =>
          list.map((task) => {
            if (task.id !== Number(tarefaId)) return task;
            const others = (task.collaborators ?? []).filter((c) => c.colaboradorId !== created.colaboradorId);
            const collaborators = [...others, created];
            const collaboratorIds = collaborators.map((c) => c.colaboradorId);
            const fullName = `${created.nome ?? ''} ${created.sobrenome ?? ''}`.trim();
            return {
              ...task,
              collaborators,
              collaboratorIds,
              memberId: task.memberId ?? created.colaboradorId,
              member: task.member || fullName,
            };
          }),
        );
        return created;
      },
      unassignCollaborator: async (tarefaId, colaboradorId) => {
        await taskApi.unassignTaskCollaborator(tarefaId, colaboradorId);
        setItems((list) =>
          list.map((task) => {
            if (task.id !== Number(tarefaId)) return task;
            const collaborators = (task.collaborators ?? []).filter(
              (c) => c.colaboradorId !== Number(colaboradorId),
            );
            const collaboratorIds = collaborators.map((c) => c.colaboradorId);
            const first = collaborators[0];
            return {
              ...task,
              collaborators,
              collaboratorIds,
              memberId: first?.colaboradorId ?? null,
              member: first ? `${first.nome ?? ''} ${first.sobrenome ?? ''}`.trim() : '',
            };
          }),
        );
      },
      // Prioridades e categorias (V8)
      addPriority: async (payload) => {
        const created = await priorityApi.createPriority(payload);
        setPriorities((list) => [...list, created]);
        return created;
      },
      addCategory: async (payload) => {
        const created = await taskApi.createTaskCategory(payload);
        setCategories((list) => [...list, created]);
        return created;
      },
      listTaskPhaseLinks: async (params) => taskApi.listTaskPhaseAssociations(params),
      addTaskPhaseLink: async (payload) => taskApi.createTaskPhaseAssociation(payload),
      removeTaskPhaseLink: async (projetoId, faseId, tarefaId) =>
        taskApi.deleteTaskPhaseAssociation(projetoId, faseId, tarefaId),
    }),
    [categories, error, isLoading, items, phases, priorities, reload],
  );

  return <TasksContext.Provider value={value}>{children}</TasksContext.Provider>;
}

export function useTasks() {
  const value = useContext(TasksContext);
  if (!value) throw new Error('TasksProvider: Provider ausente');
  return value;
}

function mapTaskResponse(
  task,
  categoriesMap,
  phasesMap,
  projectCodeMap,
  memberMap = new Map(),
  prioritiesMap = new Map(),
) {
  const collaborators = Array.isArray(task.colaboradores) ? task.colaboradores : [];
  const collaboratorIds = Array.isArray(task.colaboradoresIds)
    ? task.colaboradoresIds
    : task.colaboradorId != null
      ? [task.colaboradorId]
      : [];
  const resolvedMember =
    memberMap.get(task.colaboradorId) ||
    (collaborators[0] ? `${collaborators[0].nome ?? ''} ${collaborators[0].sobrenome ?? ''}`.trim() : '');
  const subtasks = Array.isArray(task.subtarefas) ? task.subtarefas : [];
  const checklist =
    Array.isArray(task.checklist) && task.checklist.length > 0
      ? task.checklist
      : subtasks.map((s) => ({ id: s.id, text: s.nome, done: Boolean(s.check) }));

  return {
    id: task.id,
    name: task.nome,
    projectId: task.projetoId,
    clientId: task.clienteId,
    categoryId: task.categoriaId,
    stageId: task.fkTarefaFase,
    project: projectCodeMap.get(task.projetoId) || `Projeto #${task.projetoId}`,
    memberId: task.colaboradorId,
    member: resolvedMember,
    collaboratorIds,
    collaborators,
    start: onlyDate(task.dataInicioPrev),
    end: onlyDate(task.dataFimPrev),
    startReal: onlyDate(task.dataInicioReal),
    endReal: onlyDate(task.dataFimReal),
    status: task.status,
    priorityId: task.prioridadeId ?? null,
    priority: normalizePriority(task.prioridade || prioritiesMap.get(task.prioridadeId) || ''),
    category: categoriesMap.get(task.categoriaId) || `Categoria ${task.categoriaId}`,
    stage: phasesMap.get(task.fkTarefaFase) || `Fase ${task.fkTarefaFase}`,
    note: task.observacao || '',
    done: task.status === 'Concluído' || task.status === 'Concluido',
    checklist,
    subtasks,
    timeLogs: [],
  };
}

function normalizePriority(priority) {
  if (!priority) return '';
  const trimmed = String(priority).trim();
  if (trimmed.toLowerCase() === 'critica') return 'Crítica';
  if (trimmed.toLowerCase() === 'media') return 'Média';
  return trimmed;
}

function onlyDate(dateTime) {
  return dateTime ? String(dateTime).slice(0, 10) : '';
}

function mapRecord(record) {
  const hours = (new Date(record.fim) - new Date(record.inicio)) / 3600000;
  return {
    id: record.id,
    collaboratorId: record.colaboradorId ?? null,
    collaboratorName: record.colaboradorNome ?? '',
    date: onlyDate(record.inicio),
    start: record.inicio,
    end: record.fim,
    updatedAt: record.ultimaAtualizacao ?? '',
    hours,
    note: record.descricao || '',
  };
}
