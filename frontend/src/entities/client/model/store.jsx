import { createCollectionStore } from '@/shared/lib/create-collection-store';
import { initialClients } from './mock';

export const { Provider: ClientsProvider, useCollection: useClients } = createCollectionStore(
  initialClients,
  'ClientsProvider',
);
