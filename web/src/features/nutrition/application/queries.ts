// =====================================================================
// KineticOs — Lecturas del módulo nutrition (TanStack Query).
// =====================================================================
import { useQuery } from '@tanstack/react-query';
import { nutritionApi } from '../api/nutritionApi';
import type { RecipeFilter } from '../domain/nutritionTypes';

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

export function useDailyIntake(date: string) {
  return useQuery({ queryKey: nutritionKeys.intake(date), queryFn: () => nutritionApi.listIntake(date) });
}

export function useShoppingLists() {
  return useQuery({ queryKey: nutritionKeys.shoppingLists, queryFn: nutritionApi.listShoppingLists });
}

export function useShoppingListDetail(id: number) {
  return useQuery({ queryKey: nutritionKeys.shoppingList(id), queryFn: () => nutritionApi.getShoppingList(id) });
}
