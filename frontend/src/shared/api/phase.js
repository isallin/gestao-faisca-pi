import { createApiClient } from './http';

const phaseHttp = createApiClient();

export async function listPhases() {
  const { data } = await phaseHttp.get('/fases');
  return data;
}

export async function getPhase(id) {
  const { data } = await phaseHttp.get(`/fases/${encodeURIComponent(id)}`);
  return data;
}

export async function createPhase(payload) {
  const { data } = await phaseHttp.post('/fases', payload);
  return data;
}

export async function updatePhase(id, payload) {
  const { data } = await phaseHttp.put(`/fases/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deletePhase(id) {
  await phaseHttp.delete(`/fases/${encodeURIComponent(id)}`);
}
