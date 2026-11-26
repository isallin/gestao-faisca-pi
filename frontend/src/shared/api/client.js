import { createApiClient } from './http';

const clientHttp = createApiClient();

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

// -------- Representantes Legais (V8) --------

export async function listClientRepresentatives(clienteId) {
  const { data } = await clientHttp.get(`/clientes/${encodeURIComponent(clienteId)}/representantes`);
  return data;
}

export async function createClientRepresentative(clienteId, payload) {
  const { data } = await clientHttp.post(`/clientes/${encodeURIComponent(clienteId)}/representantes`, payload);
  return data;
}

export async function updateClientRepresentative(clienteId, representanteId, payload) {
  const { data } = await clientHttp.put(
    `/clientes/${encodeURIComponent(clienteId)}/representantes/${encodeURIComponent(representanteId)}`,
    payload,
  );
  return data;
}

export async function deleteClientRepresentative(clienteId, representanteId) {
  await clientHttp.delete(
    `/clientes/${encodeURIComponent(clienteId)}/representantes/${encodeURIComponent(representanteId)}`,
  );
}

// -------- Endereços do Cliente (V8) --------

export async function listClientAddresses(clienteId) {
  const { data } = await clientHttp.get(`/clientes/${encodeURIComponent(clienteId)}/enderecos`);
  return data;
}

export async function createClientAddress(clienteId, payload) {
  const { data } = await clientHttp.post(`/clientes/${encodeURIComponent(clienteId)}/enderecos`, payload);
  return data;
}

export async function updateClientAddress(clienteId, enderecoId, payload) {
  const { data } = await clientHttp.put(
    `/clientes/${encodeURIComponent(clienteId)}/enderecos/${encodeURIComponent(enderecoId)}`,
    payload,
  );
  return data;
}

export async function deleteClientAddress(clienteId, enderecoId) {
  await clientHttp.delete(
    `/clientes/${encodeURIComponent(clienteId)}/enderecos/${encodeURIComponent(enderecoId)}`,
  );
}
