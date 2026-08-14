import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Loader2, Pencil, Sparkles, Trash2 } from 'lucide-react';
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
import { useMealPlanDetail } from '../application/queries';
import { useAdjustMealPlanCalories, useDeleteMealPlan } from '../application/mutations';
import { MEAL_TYPE_LABELS, labelFor } from '../domain/nutritionLabels';
import { RecipeName } from '../components/RecipeName';

const shortDate = new Intl.DateTimeFormat('es-AR', { day: '2-digit', month: '2-digit' });
const fullDate = new Intl.DateTimeFormat('es-AR', { day: '2-digit', month: '2-digit', year: 'numeric' });

export function MealPlanDetailPage() {
  const params = useParams();
  const mealPlanId = Number(params.id);
  const navigate = useNavigate();
  const { data: plan, isLoading, isError } = useMealPlanDetail(mealPlanId);
  const deletePlan = useDeleteMealPlan();
  const adjustCalories = useAdjustMealPlanCalories(mealPlanId);
  const [confirmOpen, setConfirmOpen] = useState(false);

  function handleDelete() {
    deletePlan.mutate(mealPlanId, {
      onSuccess: () => navigate('/nutrition'),
      onError: (error) => toast.error(extractErrorMessage(error)),
    });
  }

  function handleAdjustCalories() {
    adjustCalories.mutate(undefined, {
      onSuccess: (updated) => toast.success(`Plan ajustado a ${updated.targetCalories} kcal/día según tu progreso.`),
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
  if (isError || !plan) {
    return <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar el plan.</p>;
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-4 pb-12">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">{plan.name}</h1>
          {plan.description && <p className="mt-1 text-sm text-muted-foreground">{plan.description}</p>}
        </div>
        <div className="flex shrink-0 gap-1">
          <Button
            variant="ghost"
            size="icon"
            aria-label="Ajustar calorías con IA según mi progreso"
            title="Ajustar calorías con IA según mi progreso"
            onClick={handleAdjustCalories}
            disabled={adjustCalories.isPending}
          >
            {adjustCalories.isPending ? <Loader2 className="animate-spin" /> : <Sparkles />}
          </Button>
          <Button variant="ghost" size="icon" aria-label="Editar" onClick={() => navigate(`/nutrition/plans/${mealPlanId}/edit`)}>
            <Pencil />
          </Button>
          <Button variant="ghost" size="icon" aria-label="Borrar" onClick={() => setConfirmOpen(true)}>
            <Trash2 />
          </Button>
        </div>
      </div>

      <div className="flex flex-wrap gap-2">
        <Badge variant="secondary">
          {shortDate.format(new Date(plan.startDate))} — {shortDate.format(new Date(plan.endDate))}
        </Badge>
        {plan.targetCalories != null && <Badge variant="secondary">{plan.targetCalories} kcal/día</Badge>}
      </div>

      <h2 className="mt-2 text-base font-semibold">Días</h2>
      <div className="flex flex-col gap-3">
        {plan.days.map((day, index) => (
          <Card key={day.id}>
            <CardContent className="flex flex-col gap-2">
              <p className="font-medium">
                Día {index + 1} <span className="font-normal text-muted-foreground">· {fullDate.format(new Date(day.planDate))}</span>
              </p>
              {day.meals.length === 0 && <p className="text-sm text-muted-foreground">Sin comidas cargadas</p>}
              {day.meals.map((meal) => (
                <div key={meal.id} className="flex items-center gap-3 text-sm">
                  <span className="w-24 shrink-0 text-muted-foreground">{labelFor(MEAL_TYPE_LABELS, meal.mealType)}</span>
                  <RecipeName recipeId={meal.recipeId} className="flex-1" />
                  <span className="text-muted-foreground">x{meal.servings}</span>
                </div>
              ))}
            </CardContent>
          </Card>
        ))}
      </div>

      <Dialog open={confirmOpen} onOpenChange={setConfirmOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Borrar plan</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            ¿Seguro que querés borrar "{plan.name}"? Esta acción no se puede deshacer.
          </p>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="ghost">Cancelar</Button>
            </DialogClose>
            <Button variant="destructive" onClick={handleDelete} disabled={deletePlan.isPending}>
              Borrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
