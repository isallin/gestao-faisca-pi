import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

const dashboardHttp = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

dashboardHttp.interceptors.request.use((config) => {
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

export async function getDashboard(projectCode) {
  const params = projectCode && projectCode !== 'all' ? { projeto: projectCode } : undefined;
  const { data } = await dashboardHttp.get('/dashboard', { params });
  return data;
}
