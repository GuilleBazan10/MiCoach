// =====================================================================
// MiCoach — Fila de un ejercicio planeado dentro de un día de rutina:
// nombre (clickeable, ver detalle) + series/reps ya formateados según el
// tipo de medición del ejercicio (reps vs. segundos).
// =====================================================================
import { useExercise } from '../application/queries';
import { formatSetsReps } from '../domain/workoutLabels';
import type { PlannedExercise } from '../domain/workoutTypes';
import { ExerciseName } from './ExerciseName';
import { ExerciseThumb } from './ExerciseThumb';

export function PlannedExerciseRow({ exercise }: { exercise: PlannedExercise }) {
  const { data } = useExercise(exercise.exerciseId);

  return (
    <div className="flex items-center gap-3 text-sm">
      <span className="flex size-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
        {exercise.orderIndex}
      </span>
      <ExerciseThumb imageUrl={data?.imageUrl} category={data?.category} size="sm" />
      <div className="min-w-0 flex-1">
        <ExerciseName exerciseId={exercise.exerciseId} />
        <p className="truncate text-xs text-muted-foreground">{formatSetsReps(exercise, data?.measurementType)}</p>
      </div>
    </div>
  );
}
