// Diálogo para pedirle a la IA (Ollama local, ver ADR de Fase 4) que genere una
// rutina completa a partir de un pedido en lenguaje natural.
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles } from 'lucide-react';
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
import { useGenerateWorkout } from '../application/mutations';

export function GenerateWorkoutDialog() {
  const navigate = useNavigate();
  const generateWorkout = useGenerateWorkout();
  const [open, setOpen] = useState(false);
  const [goal, setGoal] = useState('');

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
    <Dialog open={open} onOpenChange={(next) => !generateWorkout.isPending && setOpen(next)}>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">
          <Sparkles /> Generar con IA
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Generar rutina con IA</DialogTitle>
          <DialogDescription>
            Describí qué rutina querés y la IA (Ollama, corre local) arma los días y ejercicios con el
            catálogo real. Puede tardar un rato — no hay GPU, corre en CPU.
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
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="ghost" disabled={generateWorkout.isPending}>
              Cancelar
            </Button>
          </DialogClose>
          <Button onClick={handleSubmit} disabled={generateWorkout.isPending || !goal.trim()}>
            {generateWorkout.isPending ? 'Generando…' : 'Generar'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
