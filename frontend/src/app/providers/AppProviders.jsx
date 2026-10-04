import { ClientsProvider } from '@/entities/client';
import { CollaboratorsProvider } from '@/entities/collaborator';
import { ProjectsProvider } from '@/entities/project';
import { SuppliersProvider } from '@/entities/supplier';
import { TasksProvider } from '@/entities/task';

/** Compõe os stores em memória de cada entidade (dados duram só na sessão). */
export function AppProviders({ children }) {
  return (
    <ClientsProvider>
      <CollaboratorsProvider>
        <SuppliersProvider>
          <ProjectsProvider>
            <TasksProvider>{children}</TasksProvider>
          </ProjectsProvider>
        </SuppliersProvider>
      </CollaboratorsProvider>
    </ClientsProvider>
  );
}
