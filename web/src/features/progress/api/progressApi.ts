// =====================================================================
// MiCoach — Cliente REST del módulo progress (/api/v1/progress).
// Paridad con mobile/lib/features/progress/infrastructure/progress_api.dart.
// =====================================================================
import { apiClient } from '@/core/api/client';
import type { ProgressEntry, ProgressPhoto } from '../domain/progressTypes';

export const progressApi = {
  listEntries: (metricType?: string) =>
    apiClient.get<ProgressEntry[]>('/progress/entries', { params: { metricType } }).then((r) => r.data),

  addEntry: (body: { metricType: string; value: number; unit: string; notes?: string | null }) =>
    apiClient.post<ProgressEntry>('/progress/entries', body).then((r) => r.data),

  deleteEntry: (id: number) => apiClient.delete(`/progress/entries/${id}`),

  listPhotos: () => apiClient.get<ProgressPhoto[]>('/progress/photos').then((r) => r.data),

  addPhoto: (body: { photoUrl: string; angle?: string | null }) =>
    apiClient.post<ProgressPhoto>('/progress/photos', body).then((r) => r.data),

  deletePhoto: (id: number) => apiClient.delete(`/progress/photos/${id}`),
};
