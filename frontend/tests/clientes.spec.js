import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('/');
  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();
  await page.getByRole('link', { name: 'Clientes' }).click();
});

test('acessa a tela de clientes', async ({ page }) => {
  await expect(page.getByRole('heading', { name: 'Clientes' })).toBeVisible();
  await expect(page.getByRole('region', { name: 'Lista de Clientes' })).toBeVisible();
});

test('abre o cadastro e exibe campos para pessoa física e jurídica', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Cliente' }).click();

  const dialog = page.getByRole('dialog', { name: 'Novo Cliente' });
  await expect(dialog).toBeVisible();
  await expect(dialog.getByRole('tab', { name: 'Pessoa Física' })).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'Nome', exact: true })).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'Sobrenome' })).toBeVisible();

  await dialog.getByRole('tab', { name: 'Pessoa Jurídica' }).click();
  await expect(dialog.getByRole('textbox', { name: 'Razão Social' })).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'CNPJ' })).toBeVisible();
  await expect(dialog.getByRole('button', { name: 'Salvar cliente' })).toBeEnabled();
});

test('valida o nome obrigatório ao salvar um cliente vazio', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Cliente' }).click();
  await page.getByRole('button', { name: 'Salvar cliente' }).click();

  await expect(page.getByText('Informe o nome do cliente para continuar.')).toBeVisible();
});

test('cadastra uma pessoa física e mostra o cliente na listagem', async ({ page }) => {
  const clientName = `E2E Pessoa Física ${crypto.randomUUID()}`;

  await page.getByRole('button', { name: '+ Adicionar Cliente' }).click();
  await page.getByRole('textbox', { name: 'Nome', exact: true }).fill(clientName);
  await page.getByRole('textbox', { name: 'Sobrenome' }).fill('Teste');
  await page.getByRole('button', { name: 'Salvar cliente' }).click();

  await expect(page.getByRole('region', { name: 'Lista de Clientes' }).getByText(clientName)).toBeVisible();
});

test('cadastra uma pessoa jurídica e mostra a empresa na listagem', async ({ page }) => {
  const companyName = `E2E Empresa ${crypto.randomUUID()}`;

  await page.getByRole('button', { name: '+ Adicionar Cliente' }).click();
  await page.getByRole('tab', { name: 'Pessoa Jurídica' }).click();
  await page.getByRole('textbox', { name: 'Razão Social' }).fill(companyName);
  await page.getByRole('button', { name: 'Salvar cliente' }).click();

  await expect(page.getByRole('region', { name: 'Lista de Clientes' }).getByText(companyName)).toBeVisible();
});

test('pesquisa clientes pelo nome', async ({ page }) => {
  await page.getByRole('button', { name: 'Filtrar' }).click();
  await page.getByPlaceholder('Buscar em clientes').fill('Renata Siqueira');

  const clientList = page.getByRole('region', { name: 'Lista de Clientes' });
  await expect(clientList.getByText('Renata Siqueira')).toBeVisible();
  await expect(clientList.getByText('Marina e Lucas Almeida')).not.toBeVisible();
});
