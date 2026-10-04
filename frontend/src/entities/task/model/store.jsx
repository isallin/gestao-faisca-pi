import { createCollectionStore } from '@/shared/lib/create-collection-store';
import { initialTasks } from './mock';

export const { Provider: TasksProvider, useCollection: useTasks } = createCollectionStore(
  initialTasks,
  'TasksProvider',
);
