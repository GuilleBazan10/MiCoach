// =====================================================================
// MiCoach — Fila de una comida dentro de un día del plan: chip del tipo de
// comida, miniatura, nombre (clickeable) y macros de esa porción.
// docs/10-recomendaciones-coach-nutricion.md § H.4/D.3.
// =====================================================================
import { Apple, Coffee, Moon, Soup, type LucideIcon } from 'lucide-react';
import { useRecipe } from '../application/queries';
import { MEAL_TYPE_COLORS, MEAL_TYPE_LABELS, colorFor, labelFor } from '../domain/nutritionLabels';
import type { MealPlanMeal } from '../domain/nutritionTypes';
import { RecipeName } from './RecipeName';
import { RecipeThumb } from './RecipeThumb';

const MEAL_TYPE_ICONS: Record<string, LucideIcon> = {
  breakfast: Coffee,
  lunch: Soup,
  dinner: Moon,
  snack: Apple,
};

export function MealRow({ meal }: { meal: MealPlanMeal }) {
  const { data: recipe } = useRecipe(meal.recipeId);
  const Icon = (meal.mealType && MEAL_TYPE_ICONS[meal.mealType]) || Soup;

  return (
    <div className="flex items-center gap-3 text-sm">
      <span
        className={`flex shrink-0 items-center gap-1 rounded-full px-2 py-1 text-xs font-medium ${colorFor(MEAL_TYPE_COLORS, meal.mealType)}`}
      >
        <Icon className="size-3.5" /> {labelFor(MEAL_TYPE_LABELS, meal.mealType)}
      </span>
      <RecipeThumb imageUrl={recipe?.imageUrl} mealType={meal.mealType} size="sm" />
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5">
          <RecipeName recipeId={meal.recipeId} className="min-w-0 truncate" />
          <span className="shrink-0 text-xs text-muted-foreground">x{meal.servings}</span>
        </div>
        {recipe?.caloriesPerServing != null && (
          <p className="text-xs text-muted-foreground">
            {Math.round(recipe.caloriesPerServing * meal.servings)} kcal
            {recipe.proteinPerServing != null && ` · P ${Math.round(recipe.proteinPerServing * meal.servings)}g`}
            {recipe.carbsPerServing != null && ` · C ${Math.round(recipe.carbsPerServing * meal.servings)}g`}
            {recipe.fatPerServing != null && ` · G ${Math.round(recipe.fatPerServing * meal.servings)}g`}
          </p>
        )}
      </div>
    </div>
  );
}
