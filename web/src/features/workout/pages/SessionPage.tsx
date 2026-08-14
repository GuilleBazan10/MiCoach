import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Check, Plus, X } from 'lucide-react';
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
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { extractErrorMessage } from '@/core/api/apiError';
import { useSessionDetail } from '../application/queries';
import { useAbortSession, useCompleteSession, useLogSessionExercise } from '../application/mutations';
import { SESSION_STATUS_LABELS, labelFor } from '../domain/workoutLabels';
import type { Exercise } from '../domain/workoutTypes';
import { ExercisePickerDialog } from '../components/ExercisePickerDialog';
import { SessionExerciseCard } from '../components/SessionExerciseCard';

export function SessionPage() {
  const params = useParams();
  const sessionId = Number(params.id);
  const { data: session, isLoading, isError } = useSessionDetail(sessionId);

  const [pickerOpen, setPickerOpen] = useState(false);
  const [logExercise, setLogExercise] = useState<Exercise | null>(null);
  const [completeOpen, setCompleteOpen] = useState(false);
  const [abortOpen, setAbortOpen] = useState(false);

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

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-4 pb-12">
      <h1 className="text-xl font-semibold">Sesión de entrenamiento</h1>
      <div className="flex flex-wrap items-center gap-2">
        <Badge variant="secondary">{labelFor(SESSION_STATUS_LABELS, session.status)}</Badge>
        {session.durationSeconds != null && (
          <span className="text-sm text-muted-foreground">Duración: {Math.round(session.durationSeconds / 60)} min</span>
        )}
      </div>
      {session.notes && <p className="text-sm text-muted-foreground">{session.notes}</p>}

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

      <ExercisePickerDialog open={pickerOpen} onOpenChange={setPickerOpen} onSelect={setLogExercise} />
      {logExercise && (
        <LogExerciseDialog sessionId={sessionId} exercise={logExercise} onClose={() => setLogExercise(null)} />
      )}
      <CompleteSessionDialog sessionId={sessionId} open={completeOpen} onOpenChange={setCompleteOpen} />
      <AbortSessionDialog sessionId={sessionId} open={abortOpen} onOpenChange={setAbortOpen} />
    </div>
  );
}

function LogExerciseDialog({ sessionId, exercise, onClose }: { sessionId: number; exercise: Exercise; onClose: () => void }) {
  const logExercise = useLogSessionExercise(sessionId);
  const [sets, setSets] = useState('');
  const [weight, setWeight] = useState('');
  const [reps, setReps] = useState('');
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
      { onSuccess: onClose, onError: (error) => toast.error(extractErrorMessage(error)) },
    );
  }

  return (
    <Dialog open onOpenChange={(open) => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{exercise.name}</DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-3">
          <div>
            <Label>Series realizadas</Label>
            <Input type="number" value={sets} onChange={(e) => setSets(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Peso (kg)</Label>
            <Input inputMode="decimal" value={weight} onChange={(e) => setWeight(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Repeticiones</Label>
            <Input type="number" value={reps} onChange={(e) => setReps(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>RPE (1-10)</Label>
            <Input type="number" value={rpe} onChange={(e) => setRpe(e.target.value)} className="mt-1.5" />
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
            <Input type="number" value={minutes} onChange={(e) => setMinutes(e.target.value)} className="mt-1.5" />
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
