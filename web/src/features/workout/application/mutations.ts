// =====================================================================
// MiCoach — Mutaciones del módulo workout (TanStack Query).
// =====================================================================
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { workoutApi } from '../api/workoutApi';
import type { WorkoutDraft } from '../domain/workoutTypes';
import { workoutKeys } from './queries';

export function useCreateWorkout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (draft: WorkoutDraft) => workoutApi.createWorkout(draft),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(false) });
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(true) });
    },
  });
}

export function useGenerateWorkout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (goal: string) => workoutApi.generateWorkout(goal),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(false) });
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(true) });
    },
  });
}

export function useUpdateWorkout(id: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (draft: WorkoutDraft) => workoutApi.updateWorkout(id, draft),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(false) });
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(true) });
      queryClient.invalidateQueries({ queryKey: workoutKeys.detail(id) });
    },
  });
}

export function useDeleteWorkout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => workoutApi.deleteWorkout(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(false) });
      queryClient.invalidateQueries({ queryKey: workoutKeys.list(true) });
    },
  });
}

export function useStartSession() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { workoutId?: number | null; workoutDayId?: number | null }) =>
      workoutApi.startSession(body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: workoutKeys.sessions }),
  });
}

export function useCompleteSession(id: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { durationSeconds?: number | null; notes?: string | null }) =>
      workoutApi.completeSession(id, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: workoutKeys.sessions });
      queryClient.invalidateQueries({ queryKey: workoutKeys.session(id) });
    },
  });
}

export function useAbortSession(id: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { notes?: string | null }) => workoutApi.abortSession(id, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: workoutKeys.sessions });
      queryClient.invalidateQueries({ queryKey: workoutKeys.session(id) });
    },
  });
}

export function useLogSessionExercise(sessionId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: {
      exerciseId: number;
      setsDone?: number | null;
      weightKg?: number | null;
      reps?: number | null;
      rpe?: number | null;
    }) => workoutApi.logSessionExercise(sessionId, body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: workoutKeys.session(sessionId) }),
  });
}
