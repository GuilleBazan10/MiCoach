// =====================================================================
// KineticOs — Tarjeta de un ejercicio registrado en una sesión: miniatura +
// nombre (clickeable, ver detalle) + stats logueadas.
// =====================================================================
import { Card, CardContent } from '@/components/ui/card';
import { useExercise } from '../application/queries';
import type { SessionExercise } from '../domain/workoutTypes';
import { ExerciseName } from './ExerciseName';
import { ExerciseThumb } from './ExerciseThumb';

export function SessionExerciseCard({ exercise }: { exercise: SessionExercise }) {
  const { data } = useExercise(exercise.exerciseId);

  const stats = [
    exercise.setsDone != null ? `${exercise.setsDone} series` : null,
    exercise.reps != null ? `${exercise.reps} reps` : null,
    exercise.durationSeconds != null ? `${exercise.durationSeconds} seg` : null,
    exercise.distanceMeters != null ? `${exercise.distanceMeters} m` : null,
    exercise.weightKg != null ? `${exercise.weightKg} kg` : null,
    exercise.rpe != null ? `RPE ${exercise.rpe}` : null,
  ]
    .filter(Boolean)
    .join(' · ');

  return (
    <Card>
      <CardContent className="flex items-center gap-3 py-3">
        <ExerciseThumb imageUrl={data?.imageUrl} category={data?.category} size="sm" />
        <div className="flex min-w-0 flex-col gap-0.5">
          <ExerciseName exerciseId={exercise.exerciseId} className="text-sm font-medium" />
          <span className="text-xs text-muted-foreground">{stats}</span>
        </div>
      </CardContent>
    </Card>
  );
}
