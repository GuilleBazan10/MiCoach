// =====================================================================
// MiCoach — Lecturas del módulo workout (TanStack Query).
// =====================================================================
import { useQuery } from '@tanstack/react-query';
import { workoutApi } from '../api/workoutApi';
import type { ExerciseFilter } from '../domain/workoutTypes';

export const workoutKeys = {
  muscles: ['workout', 'muscles'] as const,
  exercises: (filter: ExerciseFilter) => ['workout', 'exercises', filter] as const,
  exercise: (id: number) => ['workout', 'exercise', id] as const,
  list: (templates: boolean) => ['workout', 'list', templates] as const,
  detail: (id: number) => ['workout', 'detail', id] as const,
  sessions: ['workout', 'sessions'] as const,
  session: (id: number) => ['workout', 'session', id] as const,
};

export function useMuscles() {
  return useQuery({ queryKey: workoutKeys.muscles, queryFn: workoutApi.listMuscles });
}

export function useExerciseCatalog(filter: ExerciseFilter) {
  return useQuery({ queryKey: workoutKeys.exercises(filter), queryFn: () => workoutApi.listExercises(filter) });
}

export function useExercise(id: number) {
  return useQuery({ queryKey: workoutKeys.exercise(id), queryFn: () => workoutApi.getExercise(id) });
}

export function useWorkoutList(templates: boolean) {
  return useQuery({ queryKey: workoutKeys.list(templates), queryFn: () => workoutApi.listWorkouts(templates) });
}

export function useWorkoutDetail(id: number) {
  return useQuery({ queryKey: workoutKeys.detail(id), queryFn: () => workoutApi.getWorkout(id) });
}

export function useSessionList() {
  return useQuery({ queryKey: workoutKeys.sessions, queryFn: workoutApi.listSessions });
}

export function useSessionDetail(id: number) {
  return useQuery({ queryKey: workoutKeys.session(id), queryFn: () => workoutApi.getSession(id) });
}
