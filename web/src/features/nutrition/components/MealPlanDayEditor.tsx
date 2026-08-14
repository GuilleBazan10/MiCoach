// Editor de un día del plan (fecha + comidas). Paridad con
// meal_plan_day_editor.dart, adaptado a actualización inmutable (React).
import { useState } from 'react';
import { Plus, Trash2, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { OptionSelect } from '@/components/option-select';
import { MEAL_TYPE_LABELS } from '../domain/nutritionLabels';
import type { MealPlanDayDraft, MealPlanMealDraft } from '../domain/nutritionTypes';
import { RecipePickerDialog } from './RecipePickerDialog';

interface MealPlanDayEditorProps {
  day: MealPlanDayDraft;
  onChange: (day: MealPlanDayDraft) => void;
  onRemove: () => void;
}

export function MealPlanDayEditor({ day, onChange, onRemove }: MealPlanDayEditorProps) {
  const [pickerOpen, setPickerOpen] = useState(false);

  function updateMeal(index: number, patch: Partial<MealPlanMealDraft>) {
    onChange({ ...day, meals: day.meals.map((m, i) => (i === index ? { ...m, ...patch } : m)) });
  }

  function removeMeal(index: number) {
    onChange({ ...day, meals: day.meals.filter((_, i) => i !== index) });
  }

  return (
    <Card>
      <CardContent className="flex flex-col gap-3">
        <div className="flex items-end gap-3">
          <div className="flex-1">
            <Label htmlFor={`plan-day-${day.planDate}`}>Fecha</Label>
            <Input
              id={`plan-day-${day.planDate}`}
              type="date"
              value={day.planDate}
              onChange={(e) => onChange({ ...day, planDate: e.target.value })}
              className="mt-1.5"
            />
          </div>
          <Button type="button" variant="ghost" size="icon" aria-label="Borrar día" onClick={onRemove}>
            <Trash2 />
          </Button>
        </div>

        <div className="flex flex-col gap-2 border-t border-border pt-3">
          {day.meals.map((meal, index) => (
            <div key={index} className="grid grid-cols-[2fr_1.3fr_0.8fr_auto] items-end gap-1.5">
              <p className="truncate text-sm" title={meal.recipeName}>
                {meal.recipeName}
              </p>
              <OptionSelect
                label=""
                value={meal.mealType}
                onChange={(v) => updateMeal(index, { mealType: v })}
                options={MEAL_TYPE_LABELS}
              />
              <div>
                <Label className="text-xs">Porc.</Label>
                <Input
                  inputMode="decimal"
                  value={meal.servings}
                  onChange={(e) => updateMeal(index, { servings: Number(e.target.value.replace(',', '.')) || 0 })}
                  className="mt-1 h-7"
                />
              </div>
              <Button type="button" variant="ghost" size="icon-sm" aria-label="Quitar comida" onClick={() => removeMeal(index)}>
                <X className="size-4" />
              </Button>
            </div>
          ))}
          <Button type="button" variant="ghost" className="self-start" onClick={() => setPickerOpen(true)}>
            <Plus /> Agregar comida
          </Button>
          <RecipePickerDialog
            open={pickerOpen}
            onOpenChange={setPickerOpen}
            onSelect={(recipe) =>
              onChange({
                ...day,
                meals: [...day.meals, { recipeId: recipe.id, recipeName: recipe.name, mealType: 'breakfast', servings: 1 }],
              })
            }
          />
        </div>
      </CardContent>
    </Card>
  );
}
