import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('/');
  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();
});

test('exibe indicadores e tarefas urgentes na dashboard', async ({ page }) => {
  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();
  await expect(page.getByText('Projetos ativos')).toBeVisible();
  await expect(page.getByText('Tarefas pendentes')).toBeVisible();
  await expect(page.getByText('Horas no mês')).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Tarefas urgentes' })).toBeVisible();
  await expect(page.getByText('Revisar planta de iluminação')).toBeVisible();
});

test('navega para tarefas, clientes e projetos pelo menu lateral', async ({ page }) => {
  await page.getByRole('link', { name: 'Tarefas' }).click();
  await expect(page.getByRole('heading', { name: 'Tarefas', exact: true })).toBeVisible();

  await page.getByRole('link', { name: 'Clientes' }).click();
  await expect(page.getByRole('heading', { name: 'Clientes' })).toBeVisible();

  await page.getByRole('link', { name: 'Projetos' }).click();
  await expect(page.getByRole('heading', { name: 'Projetos' })).toBeVisible();
});
