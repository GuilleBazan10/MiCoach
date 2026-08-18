import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Check, Plus, X } from 'lucide-react';
import { toast } from 'sonner';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { extractErrorMessage } from '@/core/api/apiError';
import { useExercise, useSessionDetail, useWorkoutDetail } from '../application/queries';
import { useAbortSession, useCompleteSession, useLogSessionExercise } from '../application/mutations';
import { SESSION_STATUS_LABELS, formatSetsReps, labelFor } from '../domain/workoutLabels';
import type { Exercise, PlannedExercise } from '../domain/workoutTypes';
import { ExercisePickerDialog } from '../components/ExercisePickerDialog';
import { ExerciseThumb } from '../components/ExerciseThumb';
import { SessionExerciseCard } from '../components/SessionExerciseCard';

/** docs/10-recomendaciones-coach-nutricion.md § E.2 — cuenta regresiva simple en el cliente. */
function useRestTimer() {
  const [secondsLeft, setSecondsLeft] = useState<number | null>(null);

  useEffect(() => {
    if (secondsLeft == null || secondsLeft <= 0) return;
    const id = setTimeout(() => {
      setSecondsLeft((s) => {
        if (s == null) return null;
        if (s <= 1) {
          toast.success('Descanso terminado');
          return null;
        }
        return s - 1;
      });
    }, 1000);
    return () => clearTimeout(id);
  }, [secondsLeft]);

  return {
    secondsLeft,
    start: (seconds: number) => setSecondsLeft(seconds),
    stop: () => setSecondsLeft(null),
  };
}

function formatClock(totalSeconds: number): string {
  const m = Math.floor(totalSeconds / 60);
  const s = totalSeconds % 60;
  return `${m}:${s.toString().padStart(2, '0')}`;
}

export function SessionPage() {
  const params = useParams();
  const sessionId = Number(params.id);
  const { data: session, isLoading, isError } = useSessionDetail(sessionId);
  const { data: workout } = useWorkoutDetail(session?.workoutId);

  const [pickerOpen, setPickerOpen] = useState(false);
  const [logExercise, setLogExercise] = useState<{ exercise: Exercise; planned?: PlannedExercise } | null>(null);
  const [completeOpen, setCompleteOpen] = useState(false);
  const [abortOpen, setAbortOpen] = useState(false);
  const restTimer = useRestTimer();

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
      </div>
    );
  }
  if (isError || !session) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar la sesión.</p>;
  }

  const isActive = session.status === 'in_progress';
  const plannedDay = workout?.days.find((d) => d.id === session.workoutDayId);

  function handleLogged(planned?: PlannedExercise) {
    setLogExercise(null);
    if (planned?.restSeconds) {
      restTimer.start(planned.restSeconds);
    }
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-4 pb-12">
      {restTimer.secondsLeft != null && (
        <div className="sticky top-2 z-10 flex items-center justify-between rounded-lg bg-primary px-4 py-2.5 text-primary-foreground shadow-md">
          <span className="font-medium">Descanso: {formatClock(restTimer.secondsLeft)}</span>
          <Button
            variant="ghost"
            size="sm"
            className="text-primary-foreground hover:bg-primary-foreground/10 hover:text-primary-foreground"
            onClick={restTimer.stop}
          >
            Saltar
          </Button>
        </div>
      )}

      <h1 className="text-xl font-semibold">Sesión de entrenamiento</h1>
      <div className="flex flex-wrap items-center gap-2">
        <Badge variant="secondary">{labelFor(SESSION_STATUS_LABELS, session.status)}</Badge>
        {session.durationSeconds != null && (
          <span className="text-sm text-muted-foreground">Duración: {Math.round(session.durationSeconds / 60)} min</span>
        )}
      </div>
      {session.notes && <p className="text-sm text-muted-foreground">{session.notes}</p>}

      {isActive && plannedDay && plannedDay.exercises.length > 0 && (
        <>
          <h2 className="mt-2 text-base font-semibold">Plan de hoy</h2>
          <div className="flex flex-col gap-2">
            {plannedDay.exercises.map((planned) => (
              <PlannedSessionRow
                key={planned.id}
                planned={planned}
                onRegister={(exercise) => setLogExercise({ exercise, planned })}
              />
            ))}
          </div>
        </>
      )}

      <h2 className="mt-2 text-base font-semibold">Ejercicios registrados</h2>
      {session.exercises.length === 0 && (
        <p className="text-sm text-muted-foreground">Todavía no registraste ningún ejercicio.</p>
      )}
      <div className="flex flex-col gap-2">
        {session.exercises.map((exercise) => (
          <SessionExerciseCard key={exercise.id} exercise={exercise} />
        ))}
      </div>

      {isActive && (
        <div className="mt-2 flex flex-col gap-2">
          <Button onClick={() => setPickerOpen(true)}>
            <Plus /> Registrar ejercicio
          </Button>
          <Button variant="secondary" onClick={() => setCompleteOpen(true)}>
            <Check /> Completar sesión
          </Button>
          <Button variant="outline" onClick={() => setAbortOpen(true)}>
            <X /> Abandonar sesión
          </Button>
        </div>
      )}

      <ExercisePickerDialog
        open={pickerOpen}
        onOpenChange={setPickerOpen}
        onSelect={(exercise) => setLogExercise({ exercise })}
      />
      {logExercise && (
        <LogExerciseDialog
          sessionId={sessionId}
          exercise={logExercise.exercise}
          planned={logExercise.planned}
          onClose={() => setLogExercise(null)}
          onLogged={() => handleLogged(logExercise.planned)}
        />
      )}
      <CompleteSessionDialog sessionId={sessionId} open={completeOpen} onOpenChange={setCompleteOpen} />
      <AbortSessionDialog sessionId={sessionId} open={abortOpen} onOpenChange={setAbortOpen} />
    </div>
  );
}

/** Fila de un ejercicio planificado para hoy, con botón directo para registrarlo. */
function PlannedSessionRow({ planned, onRegister }: { planned: PlannedExercise; onRegister: (exercise: Exercise) => void }) {
  const { data: exercise } = useExercise(planned.exerciseId);

  return (
    <div className="flex items-center gap-3 rounded-lg border border-border px-3 py-2 text-sm">
      <ExerciseThumb imageUrl={exercise?.imageUrl} category={exercise?.category} size="sm" />
      <div className="min-w-0 flex-1">
        <p className="truncate font-medium">{exercise?.name ?? `Ejercicio #${planned.exerciseId}`}</p>
        <p className="text-xs text-muted-foreground">{formatSetsReps(planned, exercise?.measurementType)}</p>
      </div>
      <Button size="sm" variant="outline" className="shrink-0" disabled={!exercise} onClick={() => exercise && onRegister(exercise)}>
        Registrar
      </Button>
    </div>
  );
}

function LogExerciseDialog({
  sessionId,
  exercise,
  planned,
  onClose,
  onLogged,
}: {
  sessionId: number;
  exercise: Exercise;
  planned?: PlannedExercise;
  onClose: () => void;
  onLogged: () => void;
}) {
  const logExercise = useLogSessionExercise(sessionId);
  const [sets, setSets] = useState(planned?.sets != null ? String(planned.sets) : '');
  const [weight, setWeight] = useState('');
  const [reps, setReps] = useState(
    planned?.repsMax != null ? String(planned.repsMax) : planned?.repsMin != null ? String(planned.repsMin) : '',
  );
  const [rpe, setRpe] = useState('');

  function handleSubmit() {
    logExercise.mutate(
      {
        exerciseId: exercise.id,
        setsDone: sets ? Number(sets) : null,
        weightKg: weight ? Number(weight.replace(',', '.')) : null,
        reps: reps ? Number(reps) : null,
        rpe: rpe ? Number(rpe) : null,
      },
      { onSuccess: onLogged, onError: (error) => toast.error(extractErrorMessage(error)) },
    );
  }

  return (
    <Dialog open onOpenChange={(open) => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{exercise.name}</DialogTitle>
        </DialogHeader>
        {planned && (
          <p className="-mt-2 text-xs text-muted-foreground">Planificado: {formatSetsReps(planned)}</p>
        )}
        <div className="flex flex-col gap-3">
          <div>
            <Label>Series realizadas</Label>
            <Input inputMode="numeric" value={sets} onChange={(e) => setSets(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Peso (kg)</Label>
            <Input inputMode="decimal" value={weight} onChange={(e) => setWeight(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Repeticiones</Label>
            <Input inputMode="numeric" value={reps} onChange={(e) => setReps(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>RPE (1-10)</Label>
            <Input inputMode="numeric" value={rpe} onChange={(e) => setRpe(e.target.value)} className="mt-1.5" />
          </div>
        </div>
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="ghost">Cancelar</Button>
          </DialogClose>
          <Button onClick={handleSubmit} disabled={logExercise.isPending}>
            Registrar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function CompleteSessionDialog({
  sessionId,
  open,
  onOpenChange,
}: {
  sessionId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const completeSession = useCompleteSession(sessionId);
  const [minutes, setMinutes] = useState('');
  const [notes, setNotes] = useState('');

  function handleSubmit() {
    completeSession.mutate(
      { durationSeconds: minutes ? Number(minutes) * 60 : null, notes: notes.trim() || null },
      {
        onSuccess: () => onOpenChange(false),
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Completar sesión</DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-3">
          <div>
            <Label>Duración (minutos)</Label>
            <Input inputMode="numeric" value={minutes} onChange={(e) => setMinutes(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Notas</Label>
            <Input value={notes} onChange={(e) => setNotes(e.target.value)} className="mt-1.5" />
          </div>
        </div>
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="ghost">Cancelar</Button>
          </DialogClose>
          <Button onClick={handleSubmit} disabled={completeSession.isPending}>
            Completar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function AbortSessionDialog({
  sessionId,
  open,
  onOpenChange,
}: {
  sessionId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const abortSession = useAbortSession(sessionId);

  function handleConfirm() {
    abortSession.mutate(
      {},
      { onSuccess: () => onOpenChange(false), onError: (error) => toast.error(extractErrorMessage(error)) },
    );
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Abandonar sesión</DialogTitle>
        </DialogHeader>
        <p className="text-sm text-muted-foreground">¿Seguro que querés abandonar esta sesión?</p>
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="ghost">No</Button>
          </DialogClose>
          <Button variant="destructive" onClick={handleConfirm} disabled={abortSession.isPending}>
            Sí, abandonar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
