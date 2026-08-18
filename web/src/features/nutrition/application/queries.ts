// =====================================================================
// MiCoach — Lecturas del módulo nutrition (TanStack Query).
// =====================================================================
import { useQueries, useQuery } from '@tanstack/react-query';
import { nutritionApi } from '../api/nutritionApi';
import type { MealPlanDay, RecipeFilter } from '../domain/nutritionTypes';

export const nutritionKeys = {
  ingredients: (params: { category?: string; search?: string }) => ['nutrition', 'ingredients', params] as const,
  recipes: (filter: RecipeFilter) => ['nutrition', 'recipes', filter] as const,
  recipe: (id: number) => ['nutrition', 'recipe', id] as const,
  mealPlans: ['nutrition', 'meal-plans'] as const,
  mealPlan: (id: number) => ['nutrition', 'meal-plan', id] as const,
  intake: (date: string) => ['nutrition', 'intake', date] as const,
  shoppingLists: ['nutrition', 'shopping-lists'] as const,
  shoppingList: (id: number) => ['nutrition', 'shopping-list', id] as const,
};

export function useRecipeCatalog(filter: RecipeFilter) {
  return useQuery({ queryKey: nutritionKeys.recipes(filter), queryFn: () => nutritionApi.listRecipes(filter) });
}

export function useRecipe(id?: number | null) {
  return useQuery({
    queryKey: nutritionKeys.recipe(id ?? -1),
    queryFn: () => nutritionApi.getRecipe(id as number),
    enabled: id != null,
  });
}

export function useMealPlanList() {
  return useQuery({ queryKey: nutritionKeys.mealPlans, queryFn: nutritionApi.listMealPlans });
}

export function useMealPlanDetail(id: number) {
  return useQuery({ queryKey: nutritionKeys.mealPlan(id), queryFn: () => nutritionApi.getMealPlan(id) });
}

/**
 * Suma calorías/macros de las comidas de un día (docs/10-recomendaciones-coach-nutricion.md
 * § D.3). `null` mientras alguna receta todavía está cargando.
 */
export function useDayMacros(day: MealPlanDay): { calories: number; protein: number; carbs: number; fat: number } | null {
  const mealsWithRecipe = day.meals.filter((m) => m.recipeId != null);
  const results = useQueries({
    queries: mealsWithRecipe.map((m) => ({
      queryKey: nutritionKeys.recipe(m.recipeId as number),
      queryFn: () => nutritionApi.getRecipe(m.recipeId as number),
    })),
  });

  if (mealsWithRecipe.length === 0) {
    return { calories: 0, protein: 0, carbs: 0, fat: 0 };
  }
  if (results.some((r) => !r.data)) {
    return null;
  }

  return mealsWithRecipe.reduce(
    (acc, meal, i) => {
      const recipe = results[i].data!;
      return {
        calories: acc.calories + (recipe.caloriesPerServing ?? 0) * meal.servings,
        protein: acc.protein + (recipe.proteinPerServing ?? 0) * meal.servings,
        carbs: acc.carbs + (recipe.carbsPerServing ?? 0) * meal.servings,
        fat: acc.fat + (recipe.fatPerServing ?? 0) * meal.servings,
      };
    },
    { calories: 0, protein: 0, carbs: 0, fat: 0 },
  );
}

export function useDailyIntake(date: string) {
  return useQuery({ queryKey: nutritionKeys.intake(date), queryFn: () => nutritionApi.listIntake(date) });
}

export function useShoppingLists() {
  return useQuery({ queryKey: nutritionKeys.shoppingLists, queryFn: nutritionApi.listShoppingLists });
}

export function useShoppingListDetail(id: number) {
  return useQuery({ queryKey: nutritionKeys.shoppingList(id), queryFn: () => nutritionApi.getShoppingList(id) });
}
