// =====================================================================
// KineticOs — Lecturas del módulo progress (TanStack Query).
// =====================================================================
import { useQuery } from '@tanstack/react-query';
import { progressApi } from '../api/progressApi';

export const progressKeys = {
  entries: (metricType?: string) => ['progress', 'entries', metricType ?? null] as const,
  photos: ['progress', 'photos'] as const,
};

export function useProgressEntries(metricType?: string) {
  return useQuery({
    queryKey: progressKeys.entries(metricType),
    queryFn: () => progressApi.listEntries(metricType),
  });
}

export function useProgressPhotos() {
  return useQuery({ queryKey: progressKeys.photos, queryFn: progressApi.listPhotos });
}
