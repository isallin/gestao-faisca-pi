import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const collaboratorHttp = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

collaboratorHttp.interceptors.request.use((config) => {
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

export async function listCollaborators() {
  const { data } = await collaboratorHttp.get('/usuarios');
  return data;
}

export async function createCollaborator(payload) {
  const { data } = await collaboratorHttp.post('/usuarios', payload);
  return data;
}
