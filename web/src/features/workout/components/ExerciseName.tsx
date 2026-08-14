// Resuelve y muestra el nombre de un ejercicio por id (los planned exercises
// del backend solo traen exerciseId). Paridad con exercise_name_text.dart.
// Es clickeable: abre el detalle (imagen/video/instrucciones) del ejercicio.
import { useState } from 'react';
import { useExercise } from '../application/queries';
import { ExerciseDetailDialog } from './ExerciseDetailDialog';

export function ExerciseName({ exerciseId, className }: { exerciseId: number; className?: string }) {
  const { data, isLoading } = useExercise(exerciseId);
  const [open, setOpen] = useState(false);

  return (
    <>
      <button
        type="button"
        className={`text-left underline-offset-2 hover:underline ${className ?? ''}`}
        onClick={() => setOpen(true)}
        disabled={!data}
      >
        {isLoading ? 'Cargando…' : (data?.name ?? `Ejercicio #${exerciseId}`)}
      </button>
      <ExerciseDetailDialog exercise={data} open={open} onOpenChange={setOpen} />
    </>
  );
}
