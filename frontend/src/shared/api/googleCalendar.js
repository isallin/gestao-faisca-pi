import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const http = axios.create({ baseURL: API_BASE_URL });
http.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const getCalendarStatus = async () => (await http.get('/calendar/status')).data;
export const getConnectUrl = async () => (await http.get('/calendar/connect')).data.url;
export const disconnectCalendar = async () => http.delete('/calendar/conexao');
