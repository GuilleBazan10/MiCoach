// =====================================================================
// KineticOs — Filtro de búsqueda del catálogo de recetas. Value type
// (equality manual) para poder usarse como key de un FutureProvider.family.
// =====================================================================
class RecipeFilter {
  final String? mealCategory;
  final String? difficulty;
  final String? search;

  const RecipeFilter({this.mealCategory, this.difficulty, this.search});

  static const empty = RecipeFilter();

  Map<String, dynamic> toQueryParams() => {
        if (mealCategory != null) 'mealCategory': mealCategory,
        if (difficulty != null) 'difficulty': difficulty,
        if (search != null && search!.isNotEmpty) 'search': search,
      };

  @override
  bool operator ==(Object other) =>
      other is RecipeFilter &&
      other.mealCategory == mealCategory &&
      other.difficulty == difficulty &&
      other.search == search;

  @override
  int get hashCode => Object.hash(mealCategory, difficulty, search);
}
