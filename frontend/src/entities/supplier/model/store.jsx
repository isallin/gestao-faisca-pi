import { createCollectionStore } from '@/shared/lib/create-collection-store';
import { initialSuppliers } from './mock';

export const { Provider: SuppliersProvider, useCollection: useSuppliers } = createCollectionStore(
  initialSuppliers,
  'SuppliersProvider',
);
