// Diálogo para registrar una comida en el diario. Si se elige una receta,
// las macros se auto-completan (receta x porciones) pero siguen editables a
// mano. Paridad con log_intake_dialog.dart.
import { useState } from 'react';
import { Utensils } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { OptionSelect } from '@/components/option-select';
import { extractErrorMessage } from '@/core/api/apiError';
import { MEAL_TYPE_LABELS } from '../domain/nutritionLabels';
import type { Recipe } from '../domain/nutritionTypes';
import { useLogIntake } from '../application/mutations';
import { RecipePickerDialog } from './RecipePickerDialog';

function round1(value: number): string {
  return (Math.round(value * 10) / 10).toString();
}

export function LogIntakeDialog({ open, onOpenChange, date }: { open: boolean; onOpenChange: (open: boolean) => void; date: string }) {
  const logIntake = useLogIntake(date);
  const [mealType, setMealType] = useState('breakfast');
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [pickerOpen, setPickerOpen] = useState(false);
  const [servings, setServings] = useState('1');
  const [calories, setCalories] = useState('');
  const [protein, setProtein] = useState('');
  const [carbs, setCarbs] = useState('');
  const [fat, setFat] = useState('');

  function recalculate(selectedRecipe: Recipe, servingsValue: string) {
    const s = Number(servingsValue.replace(',', '.')) || 1;
    if (selectedRecipe.caloriesPerServing != null) setCalories(round1(selectedRecipe.caloriesPerServing * s));
    if (selectedRecipe.proteinPerServing != null) setProtein(round1(selectedRecipe.proteinPerServing * s));
    if (selectedRecipe.carbsPerServing != null) setCarbs(round1(selectedRecipe.carbsPerServing * s));
    if (selectedRecipe.fatPerServing != null) setFat(round1(selectedRecipe.fatPerServing * s));
  }

  function reset() {
    setMealType('breakfast');
    setRecipe(null);
    setServings('1');
    setCalories('');
    setProtein('');
    setCarbs('');
    setFat('');
  }

  function handleSubmit() {
    logIntake.mutate(
      {
        recipeId: recipe?.id ?? null,
        mealType,
        amount: servings ? Number(servings.replace(',', '.')) : null,
        calories: calories ? Number(calories.replace(',', '.')) : null,
        proteinG: protein ? Number(protein.replace(',', '.')) : null,
        carbsG: carbs ? Number(carbs.replace(',', '.')) : null,
        fatG: fat ? Number(fat.replace(',', '.')) : null,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          reset();
        },
        onError: (error) => toast.error(extractErrorMessage(error)),
      },
    );
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Registrar comida</DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-3">
          <OptionSelect label="Comida" value={mealType} onChange={setMealType} options={MEAL_TYPE_LABELS} />
          <Button type="button" variant="outline" className="justify-start" onClick={() => setPickerOpen(true)}>
            <Utensils /> {recipe?.name ?? 'Elegir receta (opcional)'}
          </Button>
          <RecipePickerDialog
            open={pickerOpen}
            onOpenChange={setPickerOpen}
            mealCategory={mealType}
            onSelect={(selected) => {
              setRecipe(selected);
              recalculate(selected, servings);
            }}
          />
          {recipe && (
            <div>
              <Label>Porciones</Label>
              <Input
                inputMode="decimal"
                value={servings}
                onChange={(e) => {
                  setServings(e.target.value);
                  recalculate(recipe, e.target.value);
                }}
                className="mt-1.5"
              />
            </div>
          )}
          <div>
            <Label>Calorías</Label>
            <Input inputMode="decimal" value={calories} onChange={(e) => setCalories(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Proteína (g)</Label>
            <Input inputMode="decimal" value={protein} onChange={(e) => setProtein(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Carbohidratos (g)</Label>
            <Input inputMode="decimal" value={carbs} onChange={(e) => setCarbs(e.target.value)} className="mt-1.5" />
          </div>
          <div>
            <Label>Grasas (g)</Label>
            <Input inputMode="decimal" value={fat} onChange={(e) => setFat(e.target.value)} className="mt-1.5" />
          </div>
        </div>
        <DialogFooter>
          <Button variant="ghost" onClick={() => onOpenChange(false)}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} disabled={logIntake.isPending}>
            Registrar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
