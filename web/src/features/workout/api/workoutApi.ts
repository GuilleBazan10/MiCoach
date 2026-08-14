// =====================================================================
// KineticOs — Cliente REST del módulo workout (/api/v1/workouts).
// Paridad con mobile/lib/features/workout/infrastructure/workout_api.dart.
// =====================================================================
import { apiClient } from '@/core/api/client';
import type {
  Exercise,
  ExerciseFilter,
  Muscle,
  SessionExercise,
  Workout,
  WorkoutDraft,
  WorkoutSession,
} from '../domain/workoutTypes';
import { workoutDraftToPayload } from '../domain/workoutTypes';

export const workoutApi = {
  listMuscles: () => apiClient.get<Muscle[]>('/workouts/muscles').then((r) => r.data),

  listExercises: (filter: ExerciseFilter) =>
    apiClient
      .get<Exercise[]>('/workouts/exercises', {
        params: {
          category: filter.category,
          difficulty: filter.difficulty,
          muscleId: filter.muscleId,
          search: filter.search || undefined,
        },
      })
      .then((r) => r.data),

  getExercise: (id: number) => apiClient.get<Exercise>(`/workouts/exercises/${id}`).then((r) => r.data),

  listWorkouts: (templates: boolean) =>
    apiClient.get<Workout[]>('/workouts', { params: { templates } }).then((r) => r.data),

  getWorkout: (id: number) => apiClient.get<Workout>(`/workouts/${id}`).then((r) => r.data),

  createWorkout: (draft: WorkoutDraft) =>
    apiClient.post<Workout>('/workouts', workoutDraftToPayload(draft)).then((r) => r.data),

  generateWorkout: (goal: string) =>
    // La IA local (Ollama, CPU) puede tardar bastante más que una llamada normal.
    apiClient.post<Workout>('/workouts/generate', { goal }, { timeout: 180000 }).then((r) => r.data),

  updateWorkout: (id: number, draft: WorkoutDraft) =>
    apiClient.put<Workout>(`/workouts/${id}`, workoutDraftToPayload(draft)).then((r) => r.data),

  deleteWorkout: (id: number) => apiClient.delete(`/workouts/${id}`),

  listSessions: () => apiClient.get<WorkoutSession[]>('/workouts/sessions').then((r) => r.data),

  getSession: (id: number) => apiClient.get<WorkoutSession>(`/workouts/sessions/${id}`).then((r) => r.data),

  startSession: (body: { workoutId?: number | null; workoutDayId?: number | null }) =>
    apiClient.post<WorkoutSession>('/workouts/sessions', body).then((r) => r.data),

  completeSession: (id: number, body: { durationSeconds?: number | null; notes?: string | null }) =>
    apiClient.put<WorkoutSession>(`/workouts/sessions/${id}/complete`, body).then((r) => r.data),

  abortSession: (id: number, body: { notes?: string | null }) =>
    apiClient.put<WorkoutSession>(`/workouts/sessions/${id}/abort`, body).then((r) => r.data),

  logSessionExercise: (
    sessionId: number,
    body: {
      workoutExerciseId?: number | null;
      exerciseId: number;
      setsDone?: number | null;
      weightKg?: number | null;
      reps?: number | null;
      rpe?: number | null;
      durationSeconds?: number | null;
      distanceMeters?: number | null;
      notes?: string | null;
    },
  ) => apiClient.post<SessionExercise>(`/workouts/sessions/${sessionId}/exercises`, body).then((r) => r.data),
};
