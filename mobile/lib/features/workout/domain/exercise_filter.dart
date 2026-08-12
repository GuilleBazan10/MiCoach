// =====================================================================
// KineticOs — Filtro de búsqueda del catálogo de ejercicios. Value type
// (equality manual) para poder usarse como key de un FutureProvider.family.
// =====================================================================
class ExerciseFilter {
  final String? category;
  final String? difficulty;
  final int? muscleId;
  final String? search;

  const ExerciseFilter({this.category, this.difficulty, this.muscleId, this.search});

  static const empty = ExerciseFilter();

  Map<String, dynamic> toQueryParams() => {
        if (category != null) 'category': category,
        if (difficulty != null) 'difficulty': difficulty,
        if (muscleId != null) 'muscleId': muscleId,
        if (search != null && search!.isNotEmpty) 'search': search,
      };

  @override
  bool operator ==(Object other) =>
      other is ExerciseFilter &&
      other.category == category &&
      other.difficulty == difficulty &&
      other.muscleId == muscleId &&
      other.search == search;

  @override
  int get hashCode => Object.hash(category, difficulty, muscleId, search);
}
