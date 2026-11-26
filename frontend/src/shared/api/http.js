import axios from 'axios';
import { API_BASE_URL, getAuthToken } from './auth';

/**
 * Cria uma instância Axios configurada para a API do Faísca com injeção segura do Bearer token.
 * Compatível com Axios 1.x (AxiosHeaders).
 */
export function createApiClient() {
  const client = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' },
  });

  client.interceptors.request.use((config) => {
    const token = getAuthToken();
    if (!token) return config;

    if (config.headers && typeof config.headers.set === 'function') {
      config.headers.set('Authorization', `Bearer ${token}`);
      return config;
    }

    return {
      ...config,
      headers: {
        ...(config.headers ?? {}),
        Authorization: `Bearer ${token}`,
      },
    };
  });

  return client;
}

/**
 * Extrai a mensagem de erro mais clara retornada pelo backend (ApiExceptionHandler / Spring Validation).
 */
export function extractApiErrorMessage(requestError, fallback = '') {
  const payload = requestError?.response?.data;
  if (typeof payload === 'string' && payload.trim()) return payload.trim();
  if (payload?.message && String(payload.message).trim()) return String(payload.message).trim();
  if (payload?.erro && String(payload.erro).trim()) return String(payload.erro).trim();
  if (Array.isArray(payload?.errors) && payload.errors.length > 0) {
    const first = payload.errors[0];
    return first?.defaultMessage || first?.message || fallback;
  }
  if (payload?.errors && typeof payload.errors === 'object') {
    const first = Object.values(payload.errors)[0];
    const resolved = Array.isArray(first) ? first[0] : first;
    if (resolved) return String(resolved);
  }
  return fallback;
}
