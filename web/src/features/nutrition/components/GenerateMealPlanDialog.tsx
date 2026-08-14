// Diálogo para pedirle a la IA (Ollama local, ver docs/00-progress.md § Fase 4) que
// genere un plan de alimentación completo a partir de un pedido en lenguaje natural.
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
import { useGenerateMealPlan } from '../application/mutations';

export function GenerateMealPlanDialog() {
  const navigate = useNavigate();
  const generateMealPlan = useGenerateMealPlan();
  const [open, setOpen] = useState(false);
  const [goal, setGoal] = useState('');

  function handleSubmit() {
    if (!goal.trim()) return;
    generateMealPlan.mutate(goal.trim(), {
      onSuccess: (plan) => {
        setOpen(false);
        setGoal('');
        toast.success('Plan generado con IA');
        navigate(`/nutrition/plans/${plan.id}`);
      },
      onError: (error) => toast.error(extractErrorMessage(error, 'No se pudo generar el plan.')),
    });
  }

  return (
    <Dialog open={open} onOpenChange={(next) => !generateMealPlan.isPending && setOpen(next)}>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">
          <Sparkles /> Generar con IA
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Generar plan con IA</DialogTitle>
          <DialogDescription>
            Describí qué plan querés y la IA arma los días y comidas con el catálogo de recetas real,
            teniendo en cuenta tu perfil (objetivo, calorías, patologías). Puede tardar un rato — corre local
            en CPU.
          </DialogDescription>
        </DialogHeader>
        <Textarea
          placeholder="Ej: plan para bajar de peso, 5 días, alto en proteína, sin lácteos"
          rows={4}
          value={goal}
          onChange={(e) => setGoal(e.target.value)}
          disabled={generateMealPlan.isPending}
          autoFocus
        />
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="ghost" disabled={generateMealPlan.isPending}>
              Cancelar
            </Button>
          </DialogClose>
          <Button onClick={handleSubmit} disabled={generateMealPlan.isPending || !goal.trim()}>
            {generateMealPlan.isPending ? 'Generando…' : 'Generar'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
