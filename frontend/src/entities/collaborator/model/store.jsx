import { createCollectionStore } from '@/shared/lib/create-collection-store';
import { initialCollaborators } from './mock';

export const { Provider: CollaboratorsProvider, useCollection: useCollaborators } =
  createCollectionStore(initialCollaborators, 'CollaboratorsProvider');
