import { Navigate, Outlet } from 'react-router-dom';
import { ClientCreatePage } from '@/pages/client-create';
import { ClientsPage } from '@/pages/clients';
import { CollaboratorCreatePage } from '@/pages/collaborator-create';
import { CollaboratorsPage } from '@/pages/collaborators';
import { DashboardPage } from '@/pages/dashboard';
import { EntityEditPage } from '@/pages/entity-edit';
import { LoginPage } from '@/pages/login';
import { NotFoundPage } from '@/pages/not-found';
import { ProjectCreatePage } from '@/pages/project-create';
import { ProjectsPage } from '@/pages/projects';
import { SupplierCreatePage } from '@/pages/supplier-create';
import { SuppliersPage } from '@/pages/suppliers';
import { TaskCreatePage } from '@/pages/task-create';
import { TasksPage } from '@/pages/tasks';
import { AppShell } from '@/widgets/app-shell';

function ShellLayout() {
  return (
    <AppShell>
      <Outlet />
    </AppShell>
  );
}

export const routes = [
  { path: '/', element: <Navigate to="/login" replace /> },
  { path: '/login', element: <LoginPage /> },
  {
    element: <ShellLayout />,
    children: [
      { path: '/dashboard', element: <DashboardPage /> },
      { path: '/tarefa', element: <TasksPage /> },
      { path: '/clientes', element: <ClientsPage /> },
      { path: '/colaborador', element: <CollaboratorsPage /> },
      { path: '/fornecedores', element: <SuppliersPage /> },
      { path: '/projetos', element: <ProjectsPage /> },
      { path: '/add-tarefa', element: <TaskCreatePage /> },
      { path: '/add-cliente-pessoa-fisica', element: <ClientCreatePage type="pf" /> },
      { path: '/add-cliente-juridica', element: <ClientCreatePage type="pj" /> },
      { path: '/add-colaborador', element: <CollaboratorCreatePage /> },
      { path: '/add-fornecedor', element: <SupplierCreatePage /> },
      { path: '/add-projeto', element: <ProjectCreatePage /> },
      { path: '/editar-tarefa/:id', element: <EntityEditPage kind="task" /> },
      { path: '/editar-cliente/:id', element: <EntityEditPage kind="client" /> },
      { path: '/editar-colaborador/:id', element: <EntityEditPage kind="collaborator" /> },
      { path: '/editar-fornecedor/:id', element: <EntityEditPage kind="supplier" /> },
      { path: '/editar-projeto/:id', element: <EntityEditPage kind="project" /> },
    ],
  },
  { path: '*', element: <NotFoundPage /> },
];
