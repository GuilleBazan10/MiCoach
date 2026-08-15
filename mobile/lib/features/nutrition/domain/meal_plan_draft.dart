// =====================================================================
// MiCoach — Modelos mutables para crear/editar un plan de alimentación.
// Se serializan al formato de MealPlanRequest esperado por el backend.
// =====================================================================
import 'meal_plan.dart';

class MealPlanMealDraft {
  int recipeId;
  String recipeName;
  String mealType;
  double servings;

  MealPlanMealDraft({
    required this.recipeId,
    required this.recipeName,
    this.mealType = 'breakfast',
    this.servings = 1,
  });

  factory MealPlanMealDraft.fromExisting(MealPlanMeal m, String recipeName) => MealPlanMealDraft(
        recipeId: m.recipeId ?? 0,
        recipeName: recipeName,
        mealType: m.mealType,
        servings: m.servings,
      );

  Map<String, dynamic> toJson(int orderIndex) => {
        'recipeId': recipeId,
        'mealType': mealType,
        'orderIndex': orderIndex,
        'servings': servings,
      };
}

class MealPlanDayDraft {
  DateTime planDate;
  List<MealPlanMealDraft> meals;

  MealPlanDayDraft({required this.planDate, List<MealPlanMealDraft>? meals}) : meals = meals ?? [];

  Map<String, dynamic> toJson() => {
        'planDate': planDate.toIso8601String().split('T').first,
        'meals': [for (var i = 0; i < meals.length; i++) meals[i].toJson(i + 1)],
      };
}

class MealPlanDraft {
  String name;
  String? description;
  DateTime startDate;
  DateTime endDate;
  int? targetCalories;
  List<MealPlanDayDraft> days;

  MealPlanDraft({
    this.name = '',
    this.description,
    DateTime? startDate,
    DateTime? endDate,
    this.targetCalories,
    List<MealPlanDayDraft>? days,
  })  : startDate = startDate ?? DateTime.now(),
        endDate = endDate ?? DateTime.now().add(const Duration(days: 6)),
        days = days ?? [MealPlanDayDraft(planDate: DateTime.now())];

  Map<String, dynamic> toJson() => {
        'name': name,
        'description': description,
        'startDate': startDate.toIso8601String().split('T').first,
        'endDate': endDate.toIso8601String().split('T').first,
        'targetCalories': targetCalories,
        'days': days.map((d) => d.toJson()).toList(),
      };
}
