// =====================================================================
// MiCoach — Cliente REST del módulo nutrition (/api/v1/nutrition).
// =====================================================================
import 'package:dio/dio.dart';

import '../domain/daily_intake.dart';
import '../domain/ingredient.dart';
import '../domain/meal_plan.dart';
import '../domain/meal_plan_draft.dart';
import '../domain/recipe.dart';
import '../domain/recipe_filter.dart';
import '../domain/shopping_list.dart';

class NutritionApi {
  final Dio _dio;

  NutritionApi(this._dio);

  // ------------------------- Catálogo -------------------------

  Future<List<Ingredient>> listIngredients({String? category, String? search}) async {
    final response = await _dio.get('/nutrition/ingredients',
        queryParameters: {if (category != null) 'category': category, if (search != null) 'search': search});
    return (response.data as List<dynamic>).map((e) => Ingredient.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<List<Recipe>> listRecipes(RecipeFilter filter) async {
    final response = await _dio.get('/nutrition/recipes', queryParameters: filter.toQueryParams());
    return (response.data as List<dynamic>).map((e) => Recipe.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<Recipe> getRecipe(int id) async {
    final response = await _dio.get('/nutrition/recipes/$id');
    return Recipe.fromJson(response.data as Map<String, dynamic>);
  }

  // ------------------------- Planes de alimentación -------------------------

  Future<List<MealPlan>> listMealPlans() async {
    final response = await _dio.get('/nutrition/meal-plans');
    return (response.data as List<dynamic>).map((e) => MealPlan.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<MealPlan> getMealPlan(int id) async {
    final response = await _dio.get('/nutrition/meal-plans/$id');
    return MealPlan.fromJson(response.data as Map<String, dynamic>);
  }

  Future<MealPlan> createMealPlan(MealPlanDraft draft) async {
    final response = await _dio.post('/nutrition/meal-plans', data: draft.toJson());
    return MealPlan.fromJson(response.data as Map<String, dynamic>);
  }

  Future<MealPlan> updateMealPlan(int id, MealPlanDraft draft) async {
    final response = await _dio.put('/nutrition/meal-plans/$id', data: draft.toJson());
    return MealPlan.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteMealPlan(int id) => _dio.delete('/nutrition/meal-plans/$id');

  /// Genera un plan de alimentación con IA a partir de un pedido en lenguaje natural.
  /// Puede tardar hasta un par de minutos (proveedor local por CPU) — timeout largo.
  Future<MealPlan> generateMealPlan(String goal) async {
    final response = await _dio.post(
      '/nutrition/meal-plans/generate',
      data: {'goal': goal},
      options: Options(sendTimeout: const Duration(seconds: 10), receiveTimeout: const Duration(seconds: 180)),
    );
    return MealPlan.fromJson(response.data as Map<String, dynamic>);
  }

  // ------------------------- Diario alimentario -------------------------

  Future<List<DailyIntakeEntry>> listIntake({DateTime? date}) async {
    final response = await _dio.get('/nutrition/intake', queryParameters: {
      if (date != null) 'date': date.toIso8601String().split('T').first,
    });
    return (response.data as List<dynamic>)
        .map((e) => DailyIntakeEntry.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<DailyIntakeEntry> logIntake({
    int? recipeId,
    required DateTime foodDate,
    required String mealType,
    double? amount,
    double? calories,
    double? proteinG,
    double? carbsG,
    double? fatG,
  }) async {
    final response = await _dio.post('/nutrition/intake', data: {
      'recipeId': recipeId,
      'foodDate': foodDate.toIso8601String().split('T').first,
      'mealType': mealType,
      'amount': amount,
      'calories': calories,
      'proteinG': proteinG,
      'carbsG': carbsG,
      'fatG': fatG,
    });
    return DailyIntakeEntry.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteIntake(int id) => _dio.delete('/nutrition/intake/$id');

  // ------------------------- Listas de compra -------------------------

  Future<List<ShoppingList>> listShoppingLists() async {
    final response = await _dio.get('/nutrition/shopping-lists');
    return (response.data as List<dynamic>).map((e) => ShoppingList.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<ShoppingList> getShoppingList(int id) async {
    final response = await _dio.get('/nutrition/shopping-lists/$id');
    return ShoppingList.fromJson(response.data as Map<String, dynamic>);
  }

  Future<ShoppingList> createShoppingList({String? name, DateTime? weekStart}) async {
    final response = await _dio.post('/nutrition/shopping-lists', data: {
      'name': name,
      'weekStart': weekStart?.toIso8601String().split('T').first,
    });
    return ShoppingList.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteShoppingList(int id) => _dio.delete('/nutrition/shopping-lists/$id');

  Future<ShoppingListItem> addShoppingListItem(
    int shoppingListId, {
    int? ingredientId,
    String? itemName,
    double? amount,
    String? unit,
    String? category,
  }) async {
    final response = await _dio.post('/nutrition/shopping-lists/$shoppingListId/items', data: {
      'ingredientId': ingredientId,
      'itemName': itemName,
      'amount': amount,
      'unit': unit,
      'category': category,
    });
    return ShoppingListItem.fromJson(response.data as Map<String, dynamic>);
  }

  Future<ShoppingListItem> setItemChecked(int shoppingListId, int itemId, bool checked) async {
    final response =
        await _dio.put('/nutrition/shopping-lists/$shoppingListId/items/$itemId', data: {'checked': checked});
    return ShoppingListItem.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteShoppingListItem(int shoppingListId, int itemId) =>
      _dio.delete('/nutrition/shopping-lists/$shoppingListId/items/$itemId');
}
