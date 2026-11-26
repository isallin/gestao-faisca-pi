import { createApiClient } from './http';

const http = createApiClient();

export const listSuppliers = async () => (await http.get('/fornecedores')).data;
export const getSupplier = async (id) => (await http.get(`/fornecedores/${encodeURIComponent(id)}`)).data;
export const createSupplier = async (data) => (await http.post('/fornecedores', data)).data;
export const updateSupplier = async (id, data) => (await http.put(`/fornecedores/${encodeURIComponent(id)}`, data)).data;
export const deleteSupplier = async (id) => http.delete(`/fornecedores/${encodeURIComponent(id)}`);

// -------- Categorias de Fornecedor (fornecedor_categoria V8) --------

export const listSupplierCategories = async () => (await http.get('/fornecedores/categorias')).data;
export const createSupplierCategory = async (payload) => (await http.post('/fornecedores/categorias', payload)).data;
export const updateSupplierCategory = async (id, payload) =>
  (await http.put(`/fornecedores/categorias/${encodeURIComponent(id)}`, payload)).data;
export const deleteSupplierCategory = async (id) =>
  http.delete(`/fornecedores/categorias/${encodeURIComponent(id)}`);

// -------- Contatos de Fornecedor (fornecedor_contato V8) --------

export const listSupplierContacts = async (fornecedorId) =>
  (await http.get(`/fornecedores/${encodeURIComponent(fornecedorId)}/contatos`)).data;
export const createSupplierContact = async (fornecedorId, payload) =>
  (await http.post(`/fornecedores/${encodeURIComponent(fornecedorId)}/contatos`, payload)).data;
export const updateSupplierContact = async (fornecedorId, contatoId, payload) =>
  (await http.put(
    `/fornecedores/${encodeURIComponent(fornecedorId)}/contatos/${encodeURIComponent(contatoId)}`,
    payload,
  )).data;
export const deleteSupplierContact = async (fornecedorId, contatoId) =>
  http.delete(`/fornecedores/${encodeURIComponent(fornecedorId)}/contatos/${encodeURIComponent(contatoId)}`);

// -------- Projetos do Fornecedor (fornecedor_projeto V8) --------

export const listSupplierProjects = async (fornecedorId) =>
  (await http.get(`/fornecedores/${encodeURIComponent(fornecedorId)}/projetos`)).data;
export const linkSupplierProject = async (fornecedorId, payload) =>
  (await http.post(`/fornecedores/${encodeURIComponent(fornecedorId)}/projetos`, payload)).data;
export const updateSupplierProject = async (fornecedorId, projetoId, payload) =>
  (await http.put(
    `/fornecedores/${encodeURIComponent(fornecedorId)}/projetos/${encodeURIComponent(projetoId)}`,
    payload,
  )).data;
export const unlinkSupplierProject = async (fornecedorId, projetoId) =>
  http.delete(`/fornecedores/${encodeURIComponent(fornecedorId)}/projetos/${encodeURIComponent(projetoId)}`);
