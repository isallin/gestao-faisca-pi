import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const phaseHttp = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

phaseHttp.interceptors.request.use((config) => {
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

export async function listPhases() {
  const { data } = await phaseHttp.get('/fases');
  return data;
}
