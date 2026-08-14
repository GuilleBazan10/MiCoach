import { useState } from 'react';
import { Plus, Trash2 } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { extractErrorMessage } from '@/core/api/apiError';
import { useDailyIntake } from '../application/queries';
import { useDeleteIntake } from '../application/mutations';
import { MEAL_TYPE_LABELS, labelFor } from '../domain/nutritionLabels';
import { RecipeName } from './RecipeName';
import { LogIntakeDialog } from './LogIntakeDialog';

function today(): string {
  return new Date().toISOString().slice(0, 10);
}

export function DailyIntakeView() {
  const date = today();
  const { data: entries, isLoading, isError } = useDailyIntake(date);
  const deleteIntake = useDeleteIntake(date);
  const [dialogOpen, setDialogOpen] = useState(false);

  const totals = (entries ?? []).reduce(
    (acc, e) => ({
      calories: acc.calories + (e.calories ?? 0),
      protein: acc.protein + (e.proteinG ?? 0),
      carbs: acc.carbs + (e.carbsG ?? 0),
      fat: acc.fat + (e.fatG ?? 0),
    }),
    { calories: 0, protein: 0, carbs: 0, fat: 0 },
  );

  return (
    <div className="flex flex-col gap-3">
      <Card>
        <CardContent>
          <p className="font-medium">Hoy</p>
          <p className="mt-1 text-sm text-muted-foreground">
            {Math.round(totals.calories)} kcal · P {Math.round(totals.protein)}g · C {Math.round(totals.carbs)}g · G{' '}
            {Math.round(totals.fat)}g
          </p>
        </CardContent>
      </Card>

      <Button size="sm" className="self-start" onClick={() => setDialogOpen(true)}>
        <Plus /> Registrar comida
      </Button>
      <LogIntakeDialog open={dialogOpen} onOpenChange={setDialogOpen} date={date} />

      {isLoading && (
        <div className="flex justify-center py-12">
          <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-primary" />
        </div>
      )}
      {isError && <p className="py-12 text-center text-sm text-muted-foreground">No se pudo cargar el diario.</p>}
      {!isLoading && entries?.length === 0 && (
        <p className="py-8 text-center text-sm text-muted-foreground">Todavía no registraste ninguna comida hoy.</p>
      )}
      <div className="flex flex-col gap-2">
        {entries?.map((entry) => (
          <Card key={entry.id}>
            <CardContent className="flex items-center justify-between py-3">
              <div>
                <RecipeName recipeId={entry.recipeId} className="text-sm font-medium" />
                <p className="text-xs text-muted-foreground">
                  {labelFor(MEAL_TYPE_LABELS, entry.mealType)} · {entry.calories != null ? Math.round(entry.calories) : '?'} kcal
                </p>
              </div>
              <Button
                variant="ghost"
                size="icon-sm"
                aria-label="Borrar registro"
                onClick={() => deleteIntake.mutate(entry.id, { onError: (error) => toast.error(extractErrorMessage(error)) })}
              >
                <Trash2 />
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
