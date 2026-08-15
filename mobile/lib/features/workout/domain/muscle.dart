// =====================================================================
// MiCoach — Catálogo de músculos.
// =====================================================================
class Muscle {
  final int id;
  final String code;
  final String name;
  final String muscleGroup;

  const Muscle({required this.id, required this.code, required this.name, required this.muscleGroup});

  factory Muscle.fromJson(Map<String, dynamic> json) => Muscle(
        id: json['id'] as int,
        code: json['code'] as String,
        name: json['name'] as String,
        muscleGroup: json['muscleGroup'] as String,
      );
}
