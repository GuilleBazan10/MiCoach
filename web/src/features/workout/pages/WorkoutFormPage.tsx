import { useParams } from 'react-router-dom';
import { useWorkoutDetail } from '../application/queries';
import { draftFromWorkout, newWorkoutDraft } from '../domain/workoutTypes';
import { WorkoutForm } from '../components/WorkoutForm';

export function WorkoutFormPage() {
  const params = useParams();
  const workoutId = params.id ? Number(params.id) : undefined;

  if (workoutId == null) {
    return <WorkoutForm initialDraft={newWorkoutDraft()} />;
  }
  return <EditWorkoutForm workoutId={workoutId} />;
}

function EditWorkoutForm({ workoutId }: { workoutId: number }) {
  const { data, isLoading, isError } = useWorkoutDetail(workoutId);

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }
  if (isError || !data) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar la rutina.</p>;
  }
  return <WorkoutForm initialDraft={draftFromWorkout(data)} workoutId={workoutId} />;
}
