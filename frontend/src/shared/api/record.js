import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const http = axios.create({ baseURL: API_BASE_URL });
http.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const listRecords = async () => (await http.get('/registros')).data;
export const createRecord = async (payload) => (await http.post('/registros', payload)).data;
export const updateRecord = async (id, payload) => (await http.put(`/registros/${id}`, payload)).data;
export const deleteRecord = async (id) => http.delete(`/registros/${id}`);
