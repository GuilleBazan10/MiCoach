// Diálogo para pedirle a la IA (Ollama local, ver docs/00-progress.md § Fase 4) que
// genere un plan de alimentación completo a partir de un pedido en lenguaje natural.
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
import { useGenerateMealPlan } from '../application/mutations';

const PROGRESS_MESSAGES = [
  'Analizando tu perfil y objetivo...',
  'Eligiendo recetas del catálogo...',
  'Calculando macros por día...',
  'Armando el plan completo...',
] as const;

export function GenerateMealPlanDialog() {
  const navigate = useNavigate();
  const generateMealPlan = useGenerateMealPlan();
  const [open, setOpen] = useState(false);
  const [goal, setGoal] = useState('');
  const progressMessage = useRotatingMessage(PROGRESS_MESSAGES, generateMealPlan.isPending);

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
    <Dialog open={open} onOpenChange={setOpen}>
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
            teniendo en cuenta tu perfil (objetivo, calorías, patologías). Puede tardar hasta 3 minutos.
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
        {generateMealPlan.isPending && (
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
          <Button onClick={handleSubmit} disabled={generateMealPlan.isPending || !goal.trim()}>
            {generateMealPlan.isPending ? 'Generando…' : 'Generar'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
