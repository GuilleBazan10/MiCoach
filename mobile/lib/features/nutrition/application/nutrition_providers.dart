// =====================================================================
// KineticOs — Providers del módulo nutrition (DI + lecturas + acciones).
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers/core_providers.dart';
import '../domain/daily_intake.dart';
import '../domain/ingredient.dart';
import '../domain/meal_plan.dart';
import '../domain/meal_plan_draft.dart';
import '../domain/recipe.dart';
import '../domain/recipe_filter.dart';
import '../domain/shopping_list.dart';
import '../infrastructure/nutrition_api.dart';

final nutritionApiProvider = Provider<NutritionApi>((ref) => NutritionApi(ref.watch(apiClientProvider).dio));

// ------------------------- Catálogo -------------------------

final ingredientsProvider =
    FutureProvider.family<List<Ingredient>, ({String? category, String? search})>((ref, filter) {
  return ref.watch(nutritionApiProvider).listIngredients(category: filter.category, search: filter.search);
});

final recipeCatalogProvider = FutureProvider.family<List<Recipe>, RecipeFilter>(
  (ref, filter) => ref.watch(nutritionApiProvider).listRecipes(filter),
);

final recipeDetailProvider = FutureProvider.family<Recipe, int>(
  (ref, id) => ref.watch(nutritionApiProvider).getRecipe(id),
);

// ------------------------- Planes de alimentación -------------------------

final mealPlanListProvider = FutureProvider<List<MealPlan>>(
  (ref) => ref.watch(nutritionApiProvider).listMealPlans(),
);

final mealPlanDetailProvider = FutureProvider.family<MealPlan, int>(
  (ref, id) => ref.watch(nutritionApiProvider).getMealPlan(id),
);

// ------------------------- Diario alimentario -------------------------

final dailyIntakeProvider = FutureProvider.family<List<DailyIntakeEntry>, DateTime>(
  (ref, date) => ref.watch(nutritionApiProvider).listIntake(date: date),
);

// ------------------------- Listas de compra -------------------------

final shoppingListsProvider = FutureProvider<List<ShoppingList>>(
  (ref) => ref.watch(nutritionApiProvider).listShoppingLists(),
);

final shoppingListDetailProvider = FutureProvider.family<ShoppingList, int>(
  (ref, id) => ref.watch(nutritionApiProvider).getShoppingList(id),
);

// ------------------------- Acciones (mutaciones) -------------------------

final nutritionActionsProvider =
    Provider<NutritionActions>((ref) => NutritionActions(ref, ref.watch(nutritionApiProvider)));

class NutritionActions {
  final Ref _ref;
  final NutritionApi _api;

  NutritionActions(this._ref, this._api);

  Future<MealPlan> createMealPlan(MealPlanDraft draft) async {
    final created = await _api.createMealPlan(draft);
    _ref.invalidate(mealPlanListProvider);
    return created;
  }

  Future<MealPlan> updateMealPlan(int id, MealPlanDraft draft) async {
    final updated = await _api.updateMealPlan(id, draft);
    _ref.invalidate(mealPlanListProvider);
    _ref.invalidate(mealPlanDetailProvider(id));
    return updated;
  }

  Future<void> deleteMealPlan(int id) async {
    await _api.deleteMealPlan(id);
    _ref.invalidate(mealPlanListProvider);
  }

  Future<MealPlan> generateMealPlan(String goal) async {
    final generated = await _api.generateMealPlan(goal);
    _ref.invalidate(mealPlanListProvider);
    return generated;
  }

  Future<void> logIntake({
    int? recipeId,
    required DateTime foodDate,
    required String mealType,
    double? amount,
    double? calories,
    double? proteinG,
    double? carbsG,
    double? fatG,
  }) async {
    await _api.logIntake(
        recipeId: recipeId,
        foodDate: foodDate,
        mealType: mealType,
        amount: amount,
        calories: calories,
        proteinG: proteinG,
        carbsG: carbsG,
        fatG: fatG);
    _ref.invalidate(dailyIntakeProvider);
  }

  Future<void> deleteIntake(int id) async {
    await _api.deleteIntake(id);
    _ref.invalidate(dailyIntakeProvider);
  }

  Future<ShoppingList> createShoppingList({String? name, DateTime? weekStart}) async {
    final created = await _api.createShoppingList(name: name, weekStart: weekStart);
    _ref.invalidate(shoppingListsProvider);
    return created;
  }

  Future<void> deleteShoppingList(int id) async {
    await _api.deleteShoppingList(id);
    _ref.invalidate(shoppingListsProvider);
  }

  Future<void> addShoppingListItem(int shoppingListId,
      {int? ingredientId, String? itemName, double? amount, String? unit, String? category}) async {
    await _api.addShoppingListItem(shoppingListId,
        ingredientId: ingredientId, itemName: itemName, amount: amount, unit: unit, category: category);
    _ref.invalidate(shoppingListDetailProvider(shoppingListId));
  }

  Future<void> setItemChecked(int shoppingListId, int itemId, bool checked) async {
    await _api.setItemChecked(shoppingListId, itemId, checked);
    _ref.invalidate(shoppingListDetailProvider(shoppingListId));
  }

  Future<void> deleteShoppingListItem(int shoppingListId, int itemId) async {
    await _api.deleteShoppingListItem(shoppingListId, itemId);
    _ref.invalidate(shoppingListDetailProvider(shoppingListId));
  }
}
