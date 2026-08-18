// Diálogo para pedirle a la IA (Ollama local, ver ADR de Fase 4) que genere una
// rutina completa a partir de un pedido en lenguaje natural.
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Loader2, Sparkles } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import { extractErrorMessage } from '@/core/api/apiError';
import { useRotatingMessage } from '@/core/hooks/useRotatingMessage';
import { useGenerateWorkout } from '../application/mutations';

const PROGRESS_MESSAGES = [
  'Analizando tu perfil y objetivo...',
  'Seleccionando ejercicios del catálogo...',
  'Armando los días de la rutina...',
  'Ajustando series y repeticiones...',
] as const;

export function GenerateWorkoutDialog() {
  const navigate = useNavigate();
  const generateWorkout = useGenerateWorkout();
  const [open, setOpen] = useState(false);
  const [goal, setGoal] = useState('');
  const progressMessage = useRotatingMessage(PROGRESS_MESSAGES, generateWorkout.isPending);

  function handleSubmit() {
    if (!goal.trim()) return;
    generateWorkout.mutate(goal.trim(), {
      onSuccess: (workout) => {
        setOpen(false);
        setGoal('');
        toast.success('Rutina generada con IA');
        navigate(`/workouts/${workout.id}`);
      },
      onError: (error) => toast.error(extractErrorMessage(error, 'No se pudo generar la rutina.')),
    });
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">
          <Sparkles /> Generar con IA
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Generar rutina con IA</DialogTitle>
          <DialogDescription>
            Describí qué rutina querés y la IA arma los días y ejercicios con el catálogo real. Puede
            tardar hasta 3 minutos.
          </DialogDescription>
        </DialogHeader>
        <Textarea
          placeholder="Ej: rutina de fuerza, 3 días a la semana, nivel intermedio, con mancuernas"
          rows={4}
          value={goal}
          onChange={(e) => setGoal(e.target.value)}
          disabled={generateWorkout.isPending}
          autoFocus
        />
        {generateWorkout.isPending && (
          <div className="flex flex-col gap-1">
            <p className="flex items-center gap-2 text-sm text-muted-foreground">
              <Loader2 className="size-3.5 shrink-0 animate-spin" /> {progressMessage}
            </p>
            <p className="text-xs text-muted-foreground">
              Podés cerrar esta ventana — te avisamos cuando esté lista.
            </p>
          </div>
        )}
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="ghost">Cerrar</Button>
          </DialogClose>
          <Button onClick={handleSubmit} disabled={generateWorkout.isPending || !goal.trim()}>
            {generateWorkout.isPending ? 'Generando…' : 'Generar'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
