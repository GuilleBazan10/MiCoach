// =====================================================================
// KineticOs — Plan de alimentación (mirror de NutritionDtos en backend).
// Modelos de solo lectura; para crear/editar ver meal_plan_draft.dart.
// =====================================================================
class MealPlanMeal {
  final int id;
  final int? recipeId;
  final String mealType;
  final int orderIndex;
  final double servings;
  final String? notes;

  const MealPlanMeal({
    required this.id,
    this.recipeId,
    required this.mealType,
    required this.orderIndex,
    this.servings = 1,
    this.notes,
  });

  factory MealPlanMeal.fromJson(Map<String, dynamic> json) => MealPlanMeal(
        id: json['id'] as int,
        recipeId: json['recipeId'] as int?,
        mealType: json['mealType'] as String,
        orderIndex: json['orderIndex'] as int,
        servings: (json['servings'] as num?)?.toDouble() ?? 1,
        notes: json['notes'] as String?,
      );
}

class MealPlanDay {
  final int id;
  final DateTime planDate;
  final List<MealPlanMeal> meals;

  const MealPlanDay({required this.id, required this.planDate, this.meals = const []});

  factory MealPlanDay.fromJson(Map<String, dynamic> json) => MealPlanDay(
        id: json['id'] as int,
        planDate: DateTime.parse(json['planDate'] as String),
        meals: (json['meals'] as List<dynamic>? ?? const [])
            .map((e) => MealPlanMeal.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}

class MealPlan {
  final int id;
  final int userId;
  final String name;
  final String? description;
  final DateTime startDate;
  final DateTime endDate;
  final int? targetCalories;
  final double? targetProteinG;
  final double? targetCarbsG;
  final double? targetFatG;
  final bool aiGenerated;
  final String status;
  final List<MealPlanDay> days;

  const MealPlan({
    required this.id,
    required this.userId,
    required this.name,
    this.description,
    required this.startDate,
    required this.endDate,
    this.targetCalories,
    this.targetProteinG,
    this.targetCarbsG,
    this.targetFatG,
    this.aiGenerated = false,
    this.status = 'active',
    this.days = const [],
  });

  factory MealPlan.fromJson(Map<String, dynamic> json) => MealPlan(
        id: json['id'] as int,
        userId: json['userId'] as int,
        name: json['name'] as String,
        description: json['description'] as String?,
        startDate: DateTime.parse(json['startDate'] as String),
        endDate: DateTime.parse(json['endDate'] as String),
        targetCalories: json['targetCalories'] as int?,
        targetProteinG: (json['targetProteinG'] as num?)?.toDouble(),
        targetCarbsG: (json['targetCarbsG'] as num?)?.toDouble(),
        targetFatG: (json['targetFatG'] as num?)?.toDouble(),
        aiGenerated: json['aiGenerated'] as bool? ?? false,
        status: json['status'] as String? ?? 'active',
        days: (json['days'] as List<dynamic>? ?? const [])
            .map((e) => MealPlanDay.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
