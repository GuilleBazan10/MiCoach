// =====================================================================
// MiCoach — Mutaciones del módulo progress (TanStack Query).
// =====================================================================
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { progressApi } from '../api/progressApi';

export function useAddEntry() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { metricType: string; value: number; unit: string; notes?: string | null }) =>
      progressApi.addEntry(body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['progress', 'entries'] }),
  });
}

export function useDeleteEntry() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => progressApi.deleteEntry(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['progress', 'entries'] }),
  });
}

export function useAddPhoto() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { photoUrl: string; angle?: string | null }) => progressApi.addPhoto(body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['progress', 'photos'] }),
  });
}

export function useDeletePhoto() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => progressApi.deletePhoto(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['progress', 'photos'] }),
  });
}
