import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('/');
  await page.getByPlaceholder('voce@faisca.arq.br').fill(`e2e-${Date.now()}@example.invalid`);
  await page.getByPlaceholder('Digite sua senha').fill(crypto.randomUUID());
  await page.getByRole('button', { name: 'Entrar' }).click();
  await page.getByRole('link', { name: 'Tarefas' }).click();
});

test('acessa a tela de tarefas', async ({ page }) => {
  await expect(page.getByRole('heading', { name: 'Tarefas', exact: true })).toBeVisible();
  await expect(page.getByRole('tab', { name: 'Mensal' })).toBeVisible();
});

test('abre o cadastro e exibe os campos da tarefa', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Tarefa' }).click();

  const dialog = page.getByRole('dialog', { name: 'Nova Tarefa' });
  await expect(dialog).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'Nome' })).toBeVisible();
  await expect(dialog.getByRole('combobox', { name: 'Cliente' })).toBeVisible();
  await expect(dialog.getByRole('combobox', { name: 'Projeto' })).toBeVisible();
  await expect(dialog.getByRole('textbox', { name: 'Data de início' })).toBeVisible();
  await expect(dialog.getByRole('button', { name: 'Salvar tarefa' })).toBeEnabled();
});

test('fecha o cadastro de tarefa sem salvar', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Tarefa' }).click();
  await page.getByRole('button', { name: 'Cancelar' }).click();

  await expect(page.getByRole('dialog', { name: 'Nova Tarefa' })).not.toBeVisible();
  await expect(page.getByRole('heading', { name: 'Tarefas' })).toBeVisible();
});

test('valida o nome obrigatório ao salvar uma tarefa vazia', async ({ page }) => {
  await page.getByRole('button', { name: '+ Adicionar Tarefa' }).click();
  await page.getByRole('button', { name: 'Salvar tarefa' }).click();

  await expect(page.getByText('Preencha o nome da tarefa para continuar.')).toBeVisible();
});

test('cadastra uma tarefa e a exibe na listagem', async ({ page }) => {
  const taskName = `E2E Tarefa ${crypto.randomUUID()}`;

  await page.getByRole('button', { name: '+ Adicionar Tarefa' }).click();
  await page.getByRole('textbox', { name: 'Nome' }).fill(taskName);
  await page.getByRole('button', { name: 'Salvar tarefa' }).click();

  await expect(page.getByRole('heading', { name: taskName })).toBeVisible();
});

test('filtra tarefas pelo status', async ({ page }) => {
  await page.getByRole('button', { name: 'Filtrar tarefas' }).click();
  await page.getByRole('combobox').first().selectOption('Concluído');

  await expect(page.getByRole('heading', { name: 'Enviar prancha para prefeitura' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Revisar planta de iluminação' })).not.toBeVisible();
});
