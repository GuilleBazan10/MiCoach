import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Copy, Pencil, Play, PlayCircle, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { extractErrorMessage } from '@/core/api/apiError';
import { useAuth } from '@/features/auth/application/useAuth';
import { useWorkoutDetail } from '../application/queries';
import { useCloneTemplate, useDeleteWorkout, useStartSession } from '../application/mutations';
import { OBJECTIVE_LABELS, LEVEL_LABELS, labelFor, summarizeDay } from '../domain/workoutLabels';
import { PlannedExerciseRow } from '../components/PlannedExerciseRow';

export function WorkoutDetailPage() {
  const params = useParams();
  const workoutId = Number(params.id);
  const navigate = useNavigate();
  const { user } = useAuth();
  const { data: workout, isLoading, isError } = useWorkoutDetail(workoutId);
  const startSession = useStartSession();
  const deleteWorkout = useDeleteWorkout();
  const cloneTemplate = useCloneTemplate();
  const [confirmOpen, setConfirmOpen] = useState(false);

  function handleUseTemplate() {
    cloneTemplate.mutate(workoutId, {
      onSuccess: (copy) => {
        toast.success('Plantilla copiada a tus rutinas');
        navigate(`/workouts/${copy.id}`);
      },
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  function handleStart(workoutDayId?: number) {
    startSession.mutate(
      { workoutId, workoutDayId },
      {
        onSuccess: (session) => navigate(`/sessions/${session.id}`),
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  function handleDelete() {
    deleteWorkout.mutate(workoutId, {
      onSuccess: () => navigate('/workouts'),
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }
  if (isError || !workout) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar la rutina.</p>;
  }

  const isOwner = workout.userId != null && workout.userId === user?.id;

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-4 pb-12">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">{workout.name}</h1>
          {workout.description && <p className="mt-1 text-sm text-muted-foreground">{workout.description}</p>}
        </div>
        {isOwner && (
          <div className="flex shrink-0 gap-1">
            <Button variant="ghost" size="icon" aria-label="Editar" onClick={() => navigate(`/workouts/${workoutId}/edit`)}>
              <Pencil />
            </Button>
            <Button variant="ghost" size="icon" aria-label="Borrar" onClick={() => setConfirmOpen(true)}>
              <Trash2 />
            </Button>
          </div>
        )}
        {workout.template && (
          <Button size="sm" className="shrink-0" onClick={handleUseTemplate} disabled={cloneTemplate.isPending}>
            <Copy /> Usar esta plantilla
          </Button>
        )}
      </div>

      <div className="flex flex-wrap gap-2">
        {workout.objective && <Badge variant="secondary">{labelFor(OBJECTIVE_LABELS, workout.objective)}</Badge>}
        {workout.level && <Badge variant="secondary">{labelFor(LEVEL_LABELS, workout.level)}</Badge>}
        {workout.durationWeeks != null && <Badge variant="secondary">{workout.durationWeeks} semanas</Badge>}
        {workout.template && <Badge>Plantilla</Badge>}
      </div>

      <h2 className="mt-2 text-base font-semibold">Días</h2>
      <div className="flex flex-col gap-3">
        {workout.days.map((day) => (
          <Card key={day.id}>
            <CardContent className="flex flex-col gap-2">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-base font-semibold">{day.name?.trim() ? day.name : `Día ${day.dayIndex}`}</p>
                  {!day.restDay && day.exercises.length > 0 && (
                    <p className="text-xs text-muted-foreground">{summarizeDay(day)}</p>
                  )}
                </div>
                {day.restDay ? (
                  <Badge variant="secondary">Descanso</Badge>
                ) : (
                  <Button variant="outline" size="sm" onClick={() => handleStart(day.id)} disabled={startSession.isPending}>
                    <Play /> Iniciar
                  </Button>
                )}
              </div>
              {day.exercises.map((exercise) => (
                <PlannedExerciseRow key={exercise.id} exercise={exercise} />
              ))}
            </CardContent>
          </Card>
        ))}
      </div>

      <Button variant="outline" className="self-start" onClick={() => handleStart(undefined)} disabled={startSession.isPending}>
        <PlayCircle /> Iniciar sesión libre
      </Button>

      <Dialog open={confirmOpen} onOpenChange={setConfirmOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Borrar rutina</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            ¿Seguro que querés borrar "{workout.name}"? Esta acción no se puede deshacer.
          </p>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="ghost">Cancelar</Button>
            </DialogClose>
            <Button variant="destructive" onClick={handleDelete} disabled={deleteWorkout.isPending}>
              Borrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
