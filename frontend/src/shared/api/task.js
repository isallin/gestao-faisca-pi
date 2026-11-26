import { createApiClient } from './http';

const taskHttp = createApiClient();

export async function listTasks() {
  const { data } = await taskHttp.get('/tarefas');
  return data;
}

export async function createTask(payload) {
  const { data } = await taskHttp.post('/tarefas', payload);
  return data;
}

export async function getTask(id) {
  const { data } = await taskHttp.get(`/tarefas/${encodeURIComponent(id)}`);
  return data;
}

export async function updateTask(id, payload) {
  const { data } = await taskHttp.put(`/tarefas/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deleteTask(id) {
  await taskHttp.delete(`/tarefas/${encodeURIComponent(id)}`);
}

export async function updateTaskStatus(id, status) {
  const { data } = await taskHttp.patch(`/tarefas/${encodeURIComponent(id)}/status`, { status });
  return data;
}

export async function listTaskStatusHistory(id) {
  const { data } = await taskHttp.get(`/tarefas/${encodeURIComponent(id)}/status`);
  return data;
}

// -------- Categorias, Fases e Prioridades --------

export async function listTaskCategories() {
  const { data } = await taskHttp.get('/tarefas/categorias');
  return data;
}

export async function createTaskCategory(payload) {
  const { data } = await taskHttp.post('/tarefas/categorias', payload);
  return data;
}

export async function updateTaskCategory(id, payload) {
  const { data } = await taskHttp.put(`/tarefas/categorias/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deleteTaskCategory(id) {
  await taskHttp.delete(`/tarefas/categorias/${encodeURIComponent(id)}`);
}

export async function listTaskPhases() {
  const { data } = await taskHttp.get('/tarefas/fases');
  return data;
}

export async function listTaskPriorities() {
  const { data } = await taskHttp.get('/tarefas/prioridades');
  return data;
}

// -------- Subtarefas (subtarefa V8) --------

export async function listSubtasks(tarefaId) {
  const { data } = await taskHttp.get(`/tarefas/${encodeURIComponent(tarefaId)}/subtarefas`);
  return data;
}

export async function createSubtask(tarefaId, payload) {
  const { data } = await taskHttp.post(`/tarefas/${encodeURIComponent(tarefaId)}/subtarefas`, payload);
  return data;
}

export async function updateSubtask(tarefaId, subtarefaId, payload) {
  const { data } = await taskHttp.put(
    `/tarefas/${encodeURIComponent(tarefaId)}/subtarefas/${encodeURIComponent(subtarefaId)}`,
    payload,
  );
  return data;
}

export async function toggleSubtaskCheck(tarefaId, subtarefaId, check) {
  const body = check === undefined ? {} : { check };
  const { data } = await taskHttp.patch(
    `/tarefas/${encodeURIComponent(tarefaId)}/subtarefas/${encodeURIComponent(subtarefaId)}/check`,
    body,
  );
  return data;
}

export async function deleteSubtask(tarefaId, subtarefaId) {
  await taskHttp.delete(
    `/tarefas/${encodeURIComponent(tarefaId)}/subtarefas/${encodeURIComponent(subtarefaId)}`,
  );
}

// -------- Colaboradores da tarefa (colaborador_tarefa V8) --------

export async function listTaskCollaborators(tarefaId) {
  const { data } = await taskHttp.get(`/tarefas/${encodeURIComponent(tarefaId)}/colaboradores`);
  return data;
}

export async function assignTaskCollaborator(tarefaId, payload) {
  const { data } = await taskHttp.post(`/tarefas/${encodeURIComponent(tarefaId)}/colaboradores`, payload);
  return data;
}

export async function unassignTaskCollaborator(tarefaId, colaboradorId) {
  await taskHttp.delete(
    `/tarefas/${encodeURIComponent(tarefaId)}/colaboradores/${encodeURIComponent(colaboradorId)}`,
  );
}

// -------- Associação tarefa_fase (V8) --------

export async function listTaskPhaseAssociations(params) {
  const { data } = await taskHttp.get('/tarefas-fases', { params });
  return data;
}

export async function createTaskPhaseAssociation(payload) {
  const { data } = await taskHttp.post('/tarefas-fases', payload);
  return data;
}

export async function deleteTaskPhaseAssociation(projetoId, faseId, tarefaId) {
  await taskHttp.delete(
    `/tarefas-fases/${encodeURIComponent(projetoId)}/${encodeURIComponent(faseId)}/${encodeURIComponent(tarefaId)}`,
  );
}
