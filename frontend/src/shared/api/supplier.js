import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const http = axios.create({ baseURL: API_BASE_URL });
http.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const listSuppliers = async () => (await http.get('/fornecedores')).data;
export const createSupplier = async (data) => (await http.post('/fornecedores', data)).data;
export const updateSupplier = async (id, data) => (await http.put(`/fornecedores/${id}`, data)).data;
export const deleteSupplier = async (id) => http.delete(`/fornecedores/${id}`);
