import { createCollectionStore } from '@/shared/lib/create-collection-store';
import { initialProjects } from './mock';

export const { Provider: ProjectsProvider, useCollection: useProjects } = createCollectionStore(
  initialProjects,
  'ProjectsProvider',
);
