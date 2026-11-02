import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const projectHttp = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

projectHttp.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (!token) return config;

  return {
    ...config,
    headers: {
      ...(config.headers ?? {}),
      Authorization: `Bearer ${token}`,
    },
  };
});

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
