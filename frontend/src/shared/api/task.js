import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const taskHttp = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

taskHttp.interceptors.request.use((config) => {
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

export async function listTaskCategories() {
  const { data } = await taskHttp.get('/tarefas/categorias');
  return data;
}

export async function listTaskPhases() {
  const { data } = await taskHttp.get('/tarefas/fases');
  return data;
}
