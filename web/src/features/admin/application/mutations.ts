// =====================================================================
// MiCoach — Escrituras del panel admin (TanStack Query).
// =====================================================================
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { aiProviderApi } from '../api/aiProviderApi';
import type { AiProviderConfigDraft } from '../domain/aiProviderTypes';
import { adminKeys } from './queries';

export function useUpdateAiProvider(provider: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (draft: AiProviderConfigDraft) => aiProviderApi.update(provider, draft),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: adminKeys.aiProviders }),
  });
}

export function useActivateAiProvider() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (provider: string) => aiProviderApi.activate(provider),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: adminKeys.aiProviders }),
  });
}

export function useTestAiProvider() {
  return useMutation({
    mutationFn: (provider: string) => aiProviderApi.test(provider),
  });
}
