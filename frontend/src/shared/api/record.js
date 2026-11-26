import { createApiClient } from './http';

const http = createApiClient();

export const listRecords = async (params) => (await http.get('/registros', { params })).data;
export const getRecord = async (id) => (await http.get(`/registros/${encodeURIComponent(id)}`)).data;
export const createRecord = async (payload) => (await http.post('/registros', payload)).data;
export const updateRecord = async (id, payload) => (await http.put(`/registros/${encodeURIComponent(id)}`, payload)).data;
export const deleteRecord = async (id) => http.delete(`/registros/${encodeURIComponent(id)}`);
