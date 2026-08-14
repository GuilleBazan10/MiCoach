// =====================================================================
// KineticOs — Lecturas del panel admin (TanStack Query).
// =====================================================================
import { useQuery } from '@tanstack/react-query';
import { aiProviderApi } from '../api/aiProviderApi';

export const adminKeys = {
  aiProviders: ['admin', 'ai', 'providers'] as const,
};

export function useAiProviders() {
  return useQuery({ queryKey: adminKeys.aiProviders, queryFn: aiProviderApi.list });
}
