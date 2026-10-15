import { Navigate, useNavigate, useParams } from 'react-router-dom';
import { useClients } from '@/entities/client';
import { useCollaborators } from '@/entities/collaborator';
import { useProjects } from '@/entities/project';
import { useSuppliers } from '@/entities/supplier';
import { useTasks } from '@/entities/task';
import { CreateClientForm } from '@/features/create-client';
import { CreateCollaboratorForm } from '@/features/create-collaborator';
import { CreateProjectForm } from '@/features/create-project';
import { CreateSupplierForm } from '@/features/create-supplier';
import { CreateTaskForm } from '@/features/create-task';
import { usePageTitle } from '@/shared/lib/use-page-title';

/** Cada tela de edição reaproveita o formulário de cadastro, preenchido com o registro. */
const KINDS = {
  client: { useStore: useClients, Form: CreateClientForm, prop: 'client', back: '/clientes', title: 'Editar cliente' },
  collaborator: { useStore: useCollaborators, Form: CreateCollaboratorForm, prop: 'collaborator', back: '/colaborador', title: 'Editar colaborador' },
  supplier: { useStore: useSuppliers, Form: CreateSupplierForm, prop: 'supplier', back: '/fornecedores', title: 'Editar fornecedor' },
  project: { useStore: useProjects, Form: CreateProjectForm, prop: 'project', back: '/projetos', title: 'Editar projeto' },
  task: { useStore: useTasks, Form: CreateTaskForm, prop: 'task', back: '/tarefa', title: 'Editar tarefa' },
};

/** kind: 'client' | 'collaborator' | 'supplier' | 'project' | 'task' — rota `/editar-.../:id` */
export function EntityEditPage({ kind }) {
  const { useStore, Form, prop, back, title } = KINDS[kind];
  usePageTitle(title);
  const navigate = useNavigate();
  const { id } = useParams();
  const { items } = useStore();
  const record = items.find((item) => String(item.id) === id);

  if (!record) return <Navigate to={back} replace />;
  return <Form {...{ [prop]: record }} onClose={() => navigate(back)} />;
}
