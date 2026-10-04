import { useEffect } from 'react';

export function usePageTitle(title) {
  useEffect(() => {
    document.title = title ? `${title} — Faísca Arquitetura` : 'Faísca Arquitetura';
  }, [title]);
}
