// =====================================================================
// MiCoach — Mutaciones del módulo nutrition (TanStack Query).
// =====================================================================
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { nutritionApi } from '../api/nutritionApi';
import type { MealPlanDraft } from '../domain/nutritionTypes';
import { nutritionKeys } from './queries';

export function useCreateMealPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (draft: MealPlanDraft) => nutritionApi.createMealPlan(draft),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlans }),
  });
}

export function useGenerateMealPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (goal: string) => nutritionApi.generateMealPlan(goal),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlans }),
  });
}

export function useUpdateMealPlan(id: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (draft: MealPlanDraft) => nutritionApi.updateMealPlan(id, draft),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlans });
      queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlan(id) });
    },
  });
}

export function useAdjustMealPlanCalories(id: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => nutritionApi.adjustMealPlanCalories(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlans });
      queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlan(id) });
    },
  });
}

export function useDeleteMealPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => nutritionApi.deleteMealPlan(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.mealPlans }),
  });
}

export function useLogIntake(date: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: {
      recipeId?: number | null;
      mealType: string;
      amount?: number | null;
      calories?: number | null;
      proteinG?: number | null;
      carbsG?: number | null;
      fatG?: number | null;
    }) => nutritionApi.logIntake({ ...body, foodDate: date }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.intake(date) }),
  });
}

export function useDeleteIntake(date: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => nutritionApi.deleteIntake(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.intake(date) }),
  });
}

export function useCreateShoppingList() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { name?: string }) => nutritionApi.createShoppingList(body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.shoppingLists }),
  });
}

export function useDeleteShoppingList() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => nutritionApi.deleteShoppingList(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.shoppingLists }),
  });
}

export function useAddShoppingListItem(shoppingListId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (body: { itemName: string; amount?: number | null; unit?: string | null }) =>
      nutritionApi.addShoppingListItem(shoppingListId, body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.shoppingList(shoppingListId) }),
  });
}

export function useSetItemChecked(shoppingListId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ itemId, checked }: { itemId: number; checked: boolean }) =>
      nutritionApi.setItemChecked(shoppingListId, itemId, checked),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.shoppingList(shoppingListId) }),
  });
}

export function useDeleteShoppingListItem(shoppingListId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (itemId: number) => nutritionApi.deleteShoppingListItem(shoppingListId, itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: nutritionKeys.shoppingList(shoppingListId) }),
  });
}

export function useGenerateSubstitution(ingredientId: number) {
  return useMutation({
    mutationFn: ({ reason, notes }: { reason: string; notes?: string }) =>
      nutritionApi.generateSubstitution(ingredientId, reason, notes),
  });
}
