import { createApiClient } from './http';

const collaboratorHttp = createApiClient();

export async function listCollaborators() {
  const { data } = await collaboratorHttp.get('/usuarios');
  return data;
}

export async function createCollaborator(payload) {
  const { data } = await collaboratorHttp.post('/usuarios', payload);
  return data;
}

export async function getCollaborator(id) {
  const { data } = await collaboratorHttp.get(`/usuarios/${encodeURIComponent(id)}`);
  return data;
}

export async function updateCollaborator(id, payload) {
  const { data } = await collaboratorHttp.put(`/usuarios/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function patchCollaborator(id, payload) {
  const { data } = await collaboratorHttp.patch(`/usuarios/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deleteCollaborator(id) {
  await collaboratorHttp.delete(`/usuarios/${encodeURIComponent(id)}`);
}

export async function listCollaboratorTasks(id) {
  const { data } = await collaboratorHttp.get(`/usuarios/${encodeURIComponent(id)}/tarefas`);
  return data;
}
