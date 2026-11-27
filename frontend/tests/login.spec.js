import { test, expect } from '@playwright/test';

test('exibe validação ao tentar entrar com os campos vazios', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'Bem-vindo' })).toBeVisible();
  await expect(page.getByLabel('E-mail')).toBeVisible();
  await expect(page.getByPlaceholder('Digite sua senha')).toBeVisible();

  const entrarButton = page.getByRole('button', { name: 'Entrar' });
  await expect(entrarButton).toBeVisible();
  await expect(entrarButton).toBeEnabled();
  await entrarButton.click();

  await expect(page.getByText('Preencha e-mail e senha para continuar.')).toBeVisible();
});

test('mantém o login quando o e-mail tem formato inválido', async ({ page }) => {
  await page.goto('/');

  const email = page.getByPlaceholder('voce@faisca.arq.br');
  await email.fill('e-mail-invalido');
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  expect(await email.evaluate((input) => input.validity.typeMismatch)).toBe(true);

  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page).toHaveURL(/\/login$/);
});

test('exibe validação ao preencher apenas o e-mail', async ({ page }) => {
  await page.goto('/');

  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page.getByText('Preencha e-mail e senha para continuar.')).toBeVisible();
});

test('exibe validação ao preencher apenas a senha', async ({ page }) => {
  await page.goto('/');

  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page.getByText('Preencha e-mail e senha para continuar.')).toBeVisible();
});

test('entra no modo de demonstração com os campos preenchidos', async ({ page }) => {
  await page.goto('/');

  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();
});

test('redireciona para a dashboard após autenticação demonstrativa', async ({ page }) => {
  await page.goto('/');

  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page).toHaveURL(/\/dashboard$/);
});

test('sai da aplicação e retorna à tela de login', async ({ page }) => {
  await page.goto('/');

  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();

  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.getByRole('link', { name: 'Sair' }).click();

  await expect(page.getByRole('heading', { name: 'Bem-vindo' })).toBeVisible();
  await expect(page).toHaveURL(/\/login$/);
});
