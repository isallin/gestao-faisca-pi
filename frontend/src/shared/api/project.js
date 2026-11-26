import { createApiClient } from './http';

const projectHttp = createApiClient();

export async function listProjects() {
  const { data } = await projectHttp.get('/projetos');
  return data;
}

export async function createProject(payload) {
  const { data } = await projectHttp.post('/projetos', payload);
  return data;
}

export async function getProject(id) {
  const { data } = await projectHttp.get(`/projetos/${encodeURIComponent(id)}`);
  return data;
}

export async function updateProject(id, payload) {
  const { data } = await projectHttp.put(`/projetos/${encodeURIComponent(id)}`, payload);
  return data;
}

export async function deleteProject(id) {
  await projectHttp.delete(`/projetos/${encodeURIComponent(id)}`);
}

// -------- Endereço dedicado do projeto (endereco_projeto V8) --------

export async function getProjectAddress(projectId) {
  const { data } = await projectHttp.get(`/projetos/${encodeURIComponent(projectId)}/endereco`);
  return data;
}

export async function saveProjectAddress(projectId, payload) {
  const { data } = await projectHttp.put(`/projetos/${encodeURIComponent(projectId)}/endereco`, payload);
  return data;
}

export async function deleteProjectAddress(projectId) {
  await projectHttp.delete(`/projetos/${encodeURIComponent(projectId)}/endereco`);
}

// -------- Relacionamentos V8 do projeto --------

export async function listProjectSuppliers(projectId) {
  const { data } = await projectHttp.get(`/projetos/${encodeURIComponent(projectId)}/fornecedores`);
  return data;
}

export async function listProjectPhases(projectId) {
  const { data } = await projectHttp.get(`/projetos/${encodeURIComponent(projectId)}/fases`);
  return data;
}
