// Paridad con mobile/lib/features/workout/presentation/workout_labels.dart.
export const OBJECTIVE_LABELS: Record<string, string> = {
  lose_fat: 'Perder grasa',
  gain_muscle: 'Ganar músculo',
  maintain: 'Mantener',
  endurance: 'Resistencia',
  strength: 'Fuerza',
  general_health: 'Salud general',
};

export const LEVEL_LABELS: Record<string, string> = {
  beginner: 'Principiante',
  intermediate: 'Intermedio',
  advanced: 'Avanzado',
};

export const CATEGORY_LABELS: Record<string, string> = {
  strength: 'Fuerza',
  cardio: 'Cardio',
  mobility: 'Movilidad',
  flexibility: 'Flexibilidad',
  hiit: 'HIIT',
  plyometric: 'Pliometría',
};

export const DIFFICULTY_LABELS = LEVEL_LABELS;

/** Clases Tailwind (bg/text) por objetivo/categoría — le da identidad de color a cada tipo. */
export const OBJECTIVE_COLORS: Record<string, string> = {
  lose_fat: 'bg-orange-100 text-orange-700 dark:bg-orange-500/15 dark:text-orange-400',
  gain_muscle: 'bg-primary/10 text-primary',
  maintain: 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400',
  endurance: 'bg-accent/10 text-accent',
  strength: 'bg-primary/10 text-primary',
  general_health: 'bg-purple-100 text-purple-700 dark:bg-purple-500/15 dark:text-purple-400',
};

export const CATEGORY_COLORS: Record<string, string> = {
  strength: 'bg-primary/10 text-primary',
  cardio: 'bg-accent/10 text-accent',
  mobility: 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400',
  flexibility: 'bg-purple-100 text-purple-700 dark:bg-purple-500/15 dark:text-purple-400',
  hiit: 'bg-highlight/10 text-highlight',
  plyometric: 'bg-rose-100 text-rose-700 dark:bg-rose-500/15 dark:text-rose-400',
};

export function colorFor(colors: Record<string, string>, key?: string | null): string {
  return (key && colors[key]) || 'bg-muted text-muted-foreground';
}

export const SESSION_STATUS_LABELS: Record<string, string> = {
  in_progress: 'En curso',
  completed: 'Completada',
  aborted: 'Abandonada',
};

export function labelFor(labels: Record<string, string>, key?: string | null): string {
  if (!key) return '—';
  return labels[key] ?? key;
}

/**
 * "3 series · 8-12 reps" en vez del "3 x 8-12" original, más claro para quien no conoce
 * la jerga. Para ejercicios medidos en tiempo (measurementType 'duration', ej. Plancha)
 * el mismo rango numérico se expresa en segundos, no en repeticiones.
 */
export function formatSetsReps(
  exercise: { sets?: number | null; repsMin?: number | null; repsMax?: number | null },
  measurementType?: string,
): string {
  const unit = measurementType === 'duration' ? 'seg' : 'reps';
  const sets = exercise.sets != null ? `${exercise.sets} series` : null;
  const amount =
    exercise.repsMin != null && exercise.repsMax != null
      ? exercise.repsMin === exercise.repsMax
        ? `${exercise.repsMin} ${unit}`
        : `${exercise.repsMin}-${exercise.repsMax} ${unit}`
      : null;
  return [sets, amount].filter(Boolean).join(' · ') || 'Sin datos';
}
