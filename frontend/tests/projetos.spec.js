import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('/');
  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();
  await page.getByRole('link', { name: 'Projetos' }).click();
});

test('acessa a tela de projetos', async ({ page }) => {
  await expect(page.getByRole('heading', { name: 'Projetos' })).toBeVisible();
  await expect(page.getByRole('region', { name: 'Lista de Projetos' })).toBeVisible();
});

test('abre o cadastro e exibe os campos do projeto', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Projeto' }).click();

  const dialog = page.getByRole('dialog', { name: 'Novo Projeto' });
  await expect(dialog).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'Nome', exact: true })).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'Sigla/Código' })).toBeVisible();
  await expect(dialog.getByRole('combobox', { name: 'Cliente' })).toBeVisible();
  await expect(dialog.getByRole('combobox', { name: 'Status' })).toBeVisible();
  await expect(dialog.getByRole('button', { name: 'Salvar projeto' })).toBeEnabled();
});

test('impede salvar projeto sem os campos obrigatórios', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Projeto' }).click();
  await page.getByRole('button', { name: 'Salvar projeto' }).click();

  await expect(page).toHaveURL(/\/add-projeto$/);
});

test('cadastra um projeto e mostra o registro na listagem', async ({ page }) => {
  const projectName = `E2E Projeto ${crypto.randomUUID()}`;
  const projectCode = `E2E-${crypto.randomUUID().slice(0, 8)}`;

  await page.getByRole('button', { name: '+ Adicionar Projeto' }).click();
  await page.getByRole('textbox', { name: 'Nome', exact: true }).fill(projectName);
  await page.getByRole('textbox', { name: 'Sigla/Código' }).fill(projectCode);
  await page.getByRole('button', { name: 'Salvar projeto' }).click();

  await expect(page.getByRole('region', { name: 'Lista de Projetos' }).getByText(projectName)).toBeVisible();
});

test('pesquisa projetos pelo nome', async ({ page }) => {
  await page.getByRole('button', { name: 'Filtrar' }).click();
  await page.getByPlaceholder('Buscar em projetos').fill('Casa Paineiras');

  const projectList = page.getByRole('region', { name: 'Lista de Projetos' });
  await expect(projectList.getByText('Casa Paineiras')).toBeVisible();
  await expect(projectList.getByText('Ateliê Aurora')).not.toBeVisible();
});
