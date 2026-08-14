// =====================================================================
// KineticOs — Dominio del módulo nutrition (mirror de NutritionDtos backend).
// Paridad con mobile/lib/features/nutrition/domain/*.dart.
// =====================================================================
export interface Ingredient {
  id: number;
  name: string;
  category?: string | null;
  baseUnit: string;
  caloriesPer100g: number;
  proteinPer100g: number;
  carbsPer100g: number;
  fatPer100g: number;
  fiberPer100g: number;
  aiGenerated: boolean;
}

export interface RecipeIngredient {
  ingredientId: number;
  ingredientName: string;
  amount: number;
  unit: string;
  orderIndex: number;
}

export interface Substitution {
  id: number;
  ingredientId: number;
  substituteIngredientId: number;
  substituteIngredientName: string;
  reason: string;
  notes?: string | null;
}

export interface Recipe {
  id: number;
  name: string;
  description?: string | null;
  mealCategory: string;
  difficulty?: string | null;
  servings: number;
  prepTimeMin?: number | null;
  cookTimeMin?: number | null;
  caloriesPerServing?: number | null;
  proteinPerServing?: number | null;
  carbsPerServing?: number | null;
  fatPerServing?: number | null;
  fiberPerServing?: number | null;
  instructions?: string | null;
  imageUrl?: string | null;
  aiGenerated: boolean;
  ingredients: RecipeIngredient[];
}

export interface RecipeFilter {
  mealCategory?: string;
  difficulty?: string;
  search?: string;
}

// -------- Planes de alimentación --------

export interface MealPlanMeal {
  id: number;
  recipeId?: number | null;
  mealType: string;
  orderIndex: number;
  servings: number;
  notes?: string | null;
}

export interface MealPlanDay {
  id: number;
  planDate: string;
  meals: MealPlanMeal[];
}

export interface MealPlan {
  id: number;
  userId: number;
  name: string;
  description?: string | null;
  startDate: string;
  endDate: string;
  targetCalories?: number | null;
  targetProteinG?: number | null;
  targetCarbsG?: number | null;
  targetFatG?: number | null;
  aiGenerated: boolean;
  status: string;
  days: MealPlanDay[];
}

export interface MealPlanMealDraft {
  recipeId: number;
  recipeName: string;
  mealType: string;
  servings: number;
}

export interface MealPlanDayDraft {
  planDate: string;
  meals: MealPlanMealDraft[];
}

export interface MealPlanDraft {
  name: string;
  description?: string | null;
  startDate: string;
  endDate: string;
  targetCalories?: number | null;
  days: MealPlanDayDraft[];
}

function toIsoDate(date: Date): string {
  return date.toISOString().slice(0, 10);
}

export function newMealPlanDraft(): MealPlanDraft {
  const today = new Date();
  const end = new Date(today);
  end.setDate(end.getDate() + 6);
  return {
    name: '',
    startDate: toIsoDate(today),
    endDate: toIsoDate(end),
    days: [{ planDate: toIsoDate(today), meals: [] }],
  };
}

export function draftFromMealPlan(plan: MealPlan): MealPlanDraft {
  return {
    name: plan.name,
    description: plan.description,
    startDate: plan.startDate,
    endDate: plan.endDate,
    targetCalories: plan.targetCalories,
    days: plan.days.map((d) => ({
      planDate: d.planDate,
      meals: d.meals.map((m) => ({
        recipeId: m.recipeId ?? 0,
        recipeName: `Receta #${m.recipeId}`,
        mealType: m.mealType,
        servings: m.servings,
      })),
    })),
  };
}

export function mealPlanDraftToPayload(draft: MealPlanDraft) {
  return {
    name: draft.name,
    description: draft.description || null,
    startDate: draft.startDate,
    endDate: draft.endDate,
    targetCalories: draft.targetCalories ?? null,
    days: draft.days.map((day) => ({
      planDate: day.planDate,
      meals: day.meals.map((m, i) => ({
        recipeId: m.recipeId,
        mealType: m.mealType,
        orderIndex: i + 1,
        servings: m.servings,
      })),
    })),
  };
}

// -------- Diario alimentario --------

export interface DailyIntakeEntry {
  id: number;
  mealPlanMealId?: number | null;
  recipeId?: number | null;
  foodDate: string;
  mealType: string;
  amount?: number | null;
  calories?: number | null;
  proteinG?: number | null;
  carbsG?: number | null;
  fatG?: number | null;
  consumedAt: string;
}

// -------- Listas de compra --------

export interface ShoppingListItem {
  id: number;
  ingredientId?: number | null;
  itemName?: string | null;
  amount?: number | null;
  unit?: string | null;
  category?: string | null;
  checked: boolean;
}

export interface ShoppingList {
  id: number;
  name: string;
  weekStart?: string | null;
  items: ShoppingListItem[];
}
