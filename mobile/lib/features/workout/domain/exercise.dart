// =====================================================================
// MiCoach — Catálogo de ejercicios (mirror de WorkoutDtos en backend).
// =====================================================================
class ExerciseMuscle {
  final int muscleId;
  final String muscleCode;
  final String muscleName;
  final String role;

  const ExerciseMuscle(
      {required this.muscleId, required this.muscleCode, required this.muscleName, required this.role});

  factory ExerciseMuscle.fromJson(Map<String, dynamic> json) => ExerciseMuscle(
        muscleId: json['muscleId'] as int,
        muscleCode: json['muscleCode'] as String,
        muscleName: json['muscleName'] as String,
        role: json['role'] as String,
      );
}

class Exercise {
  final int id;
  final String name;
  final String? description;
  final String category;
  final List<String> equipment;
  final String difficulty;
  final String? instructions;
  final String? videoUrl;
  final String? imageUrl;
  /// 'reps' (default) o 'duration' — ej. Plancha se sostiene X segundos, no se repite X veces.
  final String measurementType;
  final bool aiGenerated;
  final List<ExerciseMuscle> muscles;

  const Exercise({
    required this.id,
    required this.name,
    this.description,
    required this.category,
    this.equipment = const [],
    required this.difficulty,
    this.instructions,
    this.videoUrl,
    this.imageUrl,
    this.measurementType = 'reps',
    this.aiGenerated = false,
    this.muscles = const [],
  });

  factory Exercise.fromJson(Map<String, dynamic> json) => Exercise(
        id: json['id'] as int,
        name: json['name'] as String,
        description: json['description'] as String?,
        category: json['category'] as String,
        equipment: (json['equipment'] as List<dynamic>? ?? const []).map((e) => e as String).toList(),
        difficulty: json['difficulty'] as String,
        instructions: json['instructions'] as String?,
        videoUrl: json['videoUrl'] as String?,
        imageUrl: json['imageUrl'] as String?,
        measurementType: json['measurementType'] as String? ?? 'reps',
        aiGenerated: json['aiGenerated'] as bool? ?? false,
        muscles: (json['muscles'] as List<dynamic>? ?? const [])
            .map((e) => ExerciseMuscle.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
