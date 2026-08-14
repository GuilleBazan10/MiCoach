// =====================================================================
// KineticOs — Cliente REST del panel admin de IA (/api/v1/admin/ai/providers).
// =====================================================================
import { apiClient } from '@/core/api/client';
import type { AiProviderConfig, AiProviderConfigDraft, AiProviderTestResult } from '../domain/aiProviderTypes';

export const aiProviderApi = {
  list: () => apiClient.get<AiProviderConfig[]>('/admin/ai/providers').then((r) => r.data),

  update: (provider: string, draft: AiProviderConfigDraft) =>
    apiClient.put<AiProviderConfig>(`/admin/ai/providers/${provider}`, draft).then((r) => r.data),

  activate: (provider: string) =>
    apiClient.post<AiProviderConfig>(`/admin/ai/providers/${provider}/activate`).then((r) => r.data),

  test: (provider: string) =>
    apiClient
      .post<AiProviderTestResult>(`/admin/ai/providers/${provider}/test`, undefined, { timeout: 60000 })
      .then((r) => r.data),
};
