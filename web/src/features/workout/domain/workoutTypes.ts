// =====================================================================
// KineticOs — Dominio del módulo workout (mirror de WorkoutDtos en backend).
// Paridad con mobile/lib/features/workout/domain/*.dart.
// =====================================================================
export interface Muscle {
  id: number;
  code: string;
  name: string;
  muscleGroup: string;
}

export interface ExerciseMuscle {
  muscleId: number;
  muscleCode: string;
  muscleName: string;
  role: string;
}

export interface Exercise {
  id: number;
  name: string;
  description?: string | null;
  category: string;
  equipment: string[];
  difficulty: string;
  instructions?: string | null;
  videoUrl?: string | null;
  imageUrl?: string | null;
  /** 'reps' (default) o 'duration' — ej. Plancha se sostiene X segundos, no se repite X veces. */
  measurementType: string;
  aiGenerated: boolean;
  muscles: ExerciseMuscle[];
}

export interface ExerciseFilter {
  category?: string;
  difficulty?: string;
  muscleId?: number;
  search?: string;
}

export interface PlannedExercise {
  id: number;
  exerciseId: number;
  orderIndex: number;
  sets?: number | null;
  repsMin?: number | null;
  repsMax?: number | null;
  restSeconds?: number | null;
  intensityPercent?: number | null;
  tempo?: string | null;
  notes?: string | null;
}

export interface WorkoutDay {
  id: number;
  dayIndex: number;
  name?: string | null;
  restDay: boolean;
  exercises: PlannedExercise[];
}

export interface Workout {
  id: number;
  userId?: number | null;
  name: string;
  description?: string | null;
  objective?: string | null;
  level?: string | null;
  durationWeeks?: number | null;
  template: boolean;
  aiGenerated: boolean;
  status: string;
  days: WorkoutDay[];
}

// -------- Draft (crear/editar, ver workout_draft.dart) --------

export interface PlannedExerciseDraft {
  exerciseId: number;
  exerciseName: string;
  sets: number;
  repsMin?: number | null;
  repsMax?: number | null;
  restSeconds?: number | null;
}

export interface WorkoutDayDraft {
  dayIndex: number;
  name?: string | null;
  restDay: boolean;
  exercises: PlannedExerciseDraft[];
}

export interface WorkoutDraft {
  name: string;
  description?: string | null;
  objective?: string | null;
  level?: string | null;
  durationWeeks?: number | null;
  days: WorkoutDayDraft[];
}

export function newWorkoutDraft(): WorkoutDraft {
  return { name: '', days: [{ dayIndex: 1, restDay: false, exercises: [] }] };
}

export function draftFromWorkout(workout: Workout): WorkoutDraft {
  return {
    name: workout.name,
    description: workout.description,
    objective: workout.objective,
    level: workout.level,
    durationWeeks: workout.durationWeeks,
    days: workout.days.map((d) => ({
      dayIndex: d.dayIndex,
      name: d.name,
      restDay: d.restDay,
      exercises: d.exercises.map((e) => ({
        exerciseId: e.exerciseId,
        exerciseName: `Ejercicio #${e.exerciseId}`,
        sets: e.sets ?? 3,
        repsMin: e.repsMin,
        repsMax: e.repsMax,
        restSeconds: e.restSeconds,
      })),
    })),
  };
}

export function workoutDraftToPayload(draft: WorkoutDraft) {
  return {
    name: draft.name,
    description: draft.description || null,
    objective: draft.objective || null,
    level: draft.level || null,
    durationWeeks: draft.durationWeeks ?? null,
    days: draft.days.map((day) => ({
      dayIndex: day.dayIndex,
      name: day.name || null,
      restDay: day.restDay,
      exercises: day.exercises.map((e, i) => ({
        exerciseId: e.exerciseId,
        orderIndex: i + 1,
        sets: e.sets,
        repsMin: e.repsMin ?? null,
        repsMax: e.repsMax ?? null,
        restSeconds: e.restSeconds ?? null,
      })),
    })),
  };
}

// -------- Sesiones --------

export interface SessionExercise {
  id: number;
  workoutExerciseId?: number | null;
  exerciseId: number;
  setsDone?: number | null;
  weightKg?: number | null;
  reps?: number | null;
  rpe?: number | null;
  durationSeconds?: number | null;
  distanceMeters?: number | null;
  notes?: string | null;
}

export interface WorkoutSession {
  id: number;
  workoutId?: number | null;
  workoutDayId?: number | null;
  status: string;
  startedAt?: string | null;
  completedAt?: string | null;
  durationSeconds?: number | null;
  notes?: string | null;
  exercises: SessionExercise[];
}
