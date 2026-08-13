// =====================================================================
// KineticOs — Catálogo de recetas (mirror de NutritionDtos en backend).
// =====================================================================
class RecipeIngredient {
  final int ingredientId;
  final String ingredientName;
  final double amount;
  final String unit;
  final int orderIndex;

  const RecipeIngredient({
    required this.ingredientId,
    required this.ingredientName,
    required this.amount,
    required this.unit,
    required this.orderIndex,
  });

  factory RecipeIngredient.fromJson(Map<String, dynamic> json) => RecipeIngredient(
        ingredientId: json['ingredientId'] as int,
        ingredientName: json['ingredientName'] as String,
        amount: (json['amount'] as num).toDouble(),
        unit: json['unit'] as String,
        orderIndex: json['orderIndex'] as int,
      );
}

class Recipe {
  final int id;
  final String name;
  final String? description;
  final String mealCategory;
  final String? difficulty;
  final int servings;
  final int? prepTimeMin;
  final int? cookTimeMin;
  final double? caloriesPerServing;
  final double? proteinPerServing;
  final double? carbsPerServing;
  final double? fatPerServing;
  final double? fiberPerServing;
  final String? instructions;
  final String? imageUrl;
  final bool aiGenerated;
  final List<RecipeIngredient> ingredients;

  const Recipe({
    required this.id,
    required this.name,
    this.description,
    required this.mealCategory,
    this.difficulty,
    this.servings = 1,
    this.prepTimeMin,
    this.cookTimeMin,
    this.caloriesPerServing,
    this.proteinPerServing,
    this.carbsPerServing,
    this.fatPerServing,
    this.fiberPerServing,
    this.instructions,
    this.imageUrl,
    this.aiGenerated = false,
    this.ingredients = const [],
  });

  factory Recipe.fromJson(Map<String, dynamic> json) => Recipe(
        id: json['id'] as int,
        name: json['name'] as String,
        description: json['description'] as String?,
        mealCategory: json['mealCategory'] as String,
        difficulty: json['difficulty'] as String?,
        servings: json['servings'] as int? ?? 1,
        prepTimeMin: json['prepTimeMin'] as int?,
        cookTimeMin: json['cookTimeMin'] as int?,
        caloriesPerServing: (json['caloriesPerServing'] as num?)?.toDouble(),
        proteinPerServing: (json['proteinPerServing'] as num?)?.toDouble(),
        carbsPerServing: (json['carbsPerServing'] as num?)?.toDouble(),
        fatPerServing: (json['fatPerServing'] as num?)?.toDouble(),
        fiberPerServing: (json['fiberPerServing'] as num?)?.toDouble(),
        instructions: json['instructions'] as String?,
        imageUrl: json['imageUrl'] as String?,
        aiGenerated: json['aiGenerated'] as bool? ?? false,
        ingredients: (json['ingredients'] as List<dynamic>? ?? const [])
            .map((e) => RecipeIngredient.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
