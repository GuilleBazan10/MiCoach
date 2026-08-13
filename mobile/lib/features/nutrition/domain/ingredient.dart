// =====================================================================
// KineticOs — Catálogo de ingredientes (mirror de NutritionDtos en backend).
// =====================================================================
class Ingredient {
  final int id;
  final String name;
  final String? category;
  final String baseUnit;
  final double caloriesPer100g;
  final double proteinPer100g;
  final double carbsPer100g;
  final double fatPer100g;
  final double fiberPer100g;
  final bool aiGenerated;

  const Ingredient({
    required this.id,
    required this.name,
    this.category,
    required this.baseUnit,
    required this.caloriesPer100g,
    required this.proteinPer100g,
    required this.carbsPer100g,
    required this.fatPer100g,
    required this.fiberPer100g,
    this.aiGenerated = false,
  });

  factory Ingredient.fromJson(Map<String, dynamic> json) => Ingredient(
        id: json['id'] as int,
        name: json['name'] as String,
        category: json['category'] as String?,
        baseUnit: json['baseUnit'] as String,
        caloriesPer100g: (json['caloriesPer100g'] as num).toDouble(),
        proteinPer100g: (json['proteinPer100g'] as num).toDouble(),
        carbsPer100g: (json['carbsPer100g'] as num).toDouble(),
        fatPer100g: (json['fatPer100g'] as num).toDouble(),
        fiberPer100g: (json['fiberPer100g'] as num).toDouble(),
        aiGenerated: json['aiGenerated'] as bool? ?? false,
      );
}
