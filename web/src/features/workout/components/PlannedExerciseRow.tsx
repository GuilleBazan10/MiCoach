// =====================================================================
// KineticOs — Fila de un ejercicio planeado dentro de un día de rutina:
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
      <ExerciseThumb imageUrl={data?.imageUrl} category={data?.category} size="sm" />
      <div className="flex min-w-0 flex-1 items-center justify-between gap-3">
        <ExerciseName exerciseId={exercise.exerciseId} />
        <span className="shrink-0 text-muted-foreground">{formatSetsReps(exercise, data?.measurementType)}</span>
      </div>
    </div>
  );
}
