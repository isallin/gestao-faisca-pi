import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const clientHttp = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

clientHttp.interceptors.request.use((config) => {
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

export async function listClients() {
  const { data } = await clientHttp.get('/clientes');
  return data;
}

export async function createClient(payload) {
  const { data } = await clientHttp.post('/clientes', payload);
  return data;
}
export async function getClient(id) {
  const { data } = await clientHttp.get(`/clientes/${encodeURIComponent(id)}`);
  return data;
}
export async function updateClient(id, payload) {
  const { data } = await clientHttp.put(`/clientes/${encodeURIComponent(id)}`, payload);
  return data;
}
export async function deleteClient(id) {
  await clientHttp.delete(`/clientes/${encodeURIComponent(id)}`);
}
