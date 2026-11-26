import { createApiClient } from './http';

const priorityHttp = createApiClient();

export async function listPriorities() {
  const { data } = await priorityHttp.get('/prioridades');
  return data;
}

export async function getPriority(id) {
  const { data } = await priorityHttp.get(`/prioridades/${encodeURIComponent(id)}`);
  return data;
}

export async function createPriority(payload) {
  const { data } = await priorityHttp.post('/prioridades', payload);
  return data;
}

export async function updatePriority(id, payload) {
  const { data } = await priorityHttp.put(`/prioridades/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deletePriority(id) {
  await priorityHttp.delete(`/prioridades/${encodeURIComponent(id)}`);
}
