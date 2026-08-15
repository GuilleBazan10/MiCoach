// =====================================================================
// MiCoach — Diario alimentario (mirror de NutritionDtos en backend).
// =====================================================================
class DailyIntakeEntry {
  final int id;
  final int? mealPlanMealId;
  final int? recipeId;
  final DateTime foodDate;
  final String mealType;
  final double? amount;
  final double? calories;
  final double? proteinG;
  final double? carbsG;
  final double? fatG;
  final DateTime consumedAt;

  const DailyIntakeEntry({
    required this.id,
    this.mealPlanMealId,
    this.recipeId,
    required this.foodDate,
    required this.mealType,
    this.amount,
    this.calories,
    this.proteinG,
    this.carbsG,
    this.fatG,
    required this.consumedAt,
  });

  factory DailyIntakeEntry.fromJson(Map<String, dynamic> json) => DailyIntakeEntry(
        id: json['id'] as int,
        mealPlanMealId: json['mealPlanMealId'] as int?,
        recipeId: json['recipeId'] as int?,
        foodDate: DateTime.parse(json['foodDate'] as String),
        mealType: json['mealType'] as String,
        amount: (json['amount'] as num?)?.toDouble(),
        calories: (json['calories'] as num?)?.toDouble(),
        proteinG: (json['proteinG'] as num?)?.toDouble(),
        carbsG: (json['carbsG'] as num?)?.toDouble(),
        fatG: (json['fatG'] as num?)?.toDouble(),
        consumedAt: DateTime.parse(json['consumedAt'] as String),
      );
}
