// =====================================================================
// MiCoach — Cliente REST del módulo nutrition (/api/v1/nutrition).
// Paridad con mobile/lib/features/nutrition/infrastructure/nutrition_api.dart.
// =====================================================================
import { apiClient } from '@/core/api/client';
import type {
  DailyIntakeEntry,
  Ingredient,
  MealPlan,
  MealPlanDraft,
  Recipe,
  RecipeFilter,
  ShoppingList,
  ShoppingListItem,
  Substitution,
} from '../domain/nutritionTypes';
import { mealPlanDraftToPayload } from '../domain/nutritionTypes';

export const nutritionApi = {
  listIngredients: (params: { category?: string; search?: string }) =>
    apiClient.get<Ingredient[]>('/nutrition/ingredients', { params }).then((r) => r.data),

  listRecipes: (filter: RecipeFilter) =>
    apiClient
      .get<Recipe[]>('/nutrition/recipes', {
        params: { mealCategory: filter.mealCategory, difficulty: filter.difficulty, search: filter.search || undefined },
      })
      .then((r) => r.data),

  getRecipe: (id: number) => apiClient.get<Recipe>(`/nutrition/recipes/${id}`).then((r) => r.data),

  listSubstitutions: (ingredientId: number) =>
    apiClient.get<Substitution[]>(`/nutrition/ingredients/${ingredientId}/substitutions`).then((r) => r.data),

  generateSubstitution: (ingredientId: number, reason: string, notes?: string) =>
    apiClient
      .post<Substitution>(`/nutrition/ingredients/${ingredientId}/substitutions/generate`, { reason, notes })
      .then((r) => r.data),

  listMealPlans: () => apiClient.get<MealPlan[]>('/nutrition/meal-plans').then((r) => r.data),

  getMealPlan: (id: number) => apiClient.get<MealPlan>(`/nutrition/meal-plans/${id}`).then((r) => r.data),

  createMealPlan: (draft: MealPlanDraft) =>
    apiClient.post<MealPlan>('/nutrition/meal-plans', mealPlanDraftToPayload(draft)).then((r) => r.data),

  generateMealPlan: (goal: string) =>
    // Igual que workoutApi.generateWorkout: la IA local (Ollama, CPU) tarda bastante más
    // que una llamada normal.
    apiClient.post<MealPlan>('/nutrition/meal-plans/generate', { goal }, { timeout: 180000 }).then((r) => r.data),

  updateMealPlan: (id: number, draft: MealPlanDraft) =>
    apiClient.put<MealPlan>(`/nutrition/meal-plans/${id}`, mealPlanDraftToPayload(draft)).then((r) => r.data),

  adjustMealPlanCalories: (id: number) =>
    apiClient.post<MealPlan>(`/nutrition/meal-plans/${id}/adjust-calories`, undefined, { timeout: 180000 }).then((r) => r.data),

  deleteMealPlan: (id: number) => apiClient.delete(`/nutrition/meal-plans/${id}`),

  listIntake: (date?: string) =>
    apiClient.get<DailyIntakeEntry[]>('/nutrition/intake', { params: { date } }).then((r) => r.data),

  logIntake: (body: {
    recipeId?: number | null;
    foodDate: string;
    mealType: string;
    amount?: number | null;
    calories?: number | null;
    proteinG?: number | null;
    carbsG?: number | null;
    fatG?: number | null;
  }) => apiClient.post<DailyIntakeEntry>('/nutrition/intake', body).then((r) => r.data),

  deleteIntake: (id: number) => apiClient.delete(`/nutrition/intake/${id}`),

  listShoppingLists: () => apiClient.get<ShoppingList[]>('/nutrition/shopping-lists').then((r) => r.data),

  getShoppingList: (id: number) => apiClient.get<ShoppingList>(`/nutrition/shopping-lists/${id}`).then((r) => r.data),

  createShoppingList: (body: { name?: string; weekStart?: string | null }) =>
    apiClient.post<ShoppingList>('/nutrition/shopping-lists', body).then((r) => r.data),

  deleteShoppingList: (id: number) => apiClient.delete(`/nutrition/shopping-lists/${id}`),

  addShoppingListItem: (
    shoppingListId: number,
    body: { ingredientId?: number | null; itemName?: string | null; amount?: number | null; unit?: string | null; category?: string | null },
  ) => apiClient.post<ShoppingListItem>(`/nutrition/shopping-lists/${shoppingListId}/items`, body).then((r) => r.data),

  setItemChecked: (shoppingListId: number, itemId: number, checked: boolean) =>
    apiClient
      .put<ShoppingListItem>(`/nutrition/shopping-lists/${shoppingListId}/items/${itemId}`, { checked })
      .then((r) => r.data),

  deleteShoppingListItem: (shoppingListId: number, itemId: number) =>
    apiClient.delete(`/nutrition/shopping-lists/${shoppingListId}/items/${itemId}`),
};
