import { ClientsProvider } from '@/entities/client';
import { CollaboratorsProvider } from '@/entities/collaborator';
import { ProjectsProvider } from '@/entities/project';
import { SuppliersProvider } from '@/entities/supplier';
import { TasksProvider } from '@/entities/task';

/** Carrega as coleções persistidas na API para as páginas da aplicação. */
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
