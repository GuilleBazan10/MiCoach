// =====================================================================
// KineticOs — Rutina de entrenamiento (mirror de WorkoutDtos en backend).
// Modelos de solo lectura; para crear/editar ver workout_draft.dart.
// =====================================================================
class PlannedExercise {
  final int id;
  final int exerciseId;
  final int orderIndex;
  final int? sets;
  final int? repsMin;
  final int? repsMax;
  final int? restSeconds;
  final int? intensityPercent;
  final String? tempo;
  final String? notes;

  const PlannedExercise({
    required this.id,
    required this.exerciseId,
    required this.orderIndex,
    this.sets,
    this.repsMin,
    this.repsMax,
    this.restSeconds,
    this.intensityPercent,
    this.tempo,
    this.notes,
  });

  factory PlannedExercise.fromJson(Map<String, dynamic> json) => PlannedExercise(
        id: json['id'] as int,
        exerciseId: json['exerciseId'] as int,
        orderIndex: json['orderIndex'] as int,
        sets: json['sets'] as int?,
        repsMin: json['repsMin'] as int?,
        repsMax: json['repsMax'] as int?,
        restSeconds: json['restSeconds'] as int?,
        intensityPercent: json['intensityPercent'] as int?,
        tempo: json['tempo'] as String?,
        notes: json['notes'] as String?,
      );
}

class WorkoutDay {
  final int id;
  final int dayIndex;
  final String? name;
  final bool restDay;
  final List<PlannedExercise> exercises;

  const WorkoutDay({
    required this.id,
    required this.dayIndex,
    this.name,
    this.restDay = false,
    this.exercises = const [],
  });

  factory WorkoutDay.fromJson(Map<String, dynamic> json) => WorkoutDay(
        id: json['id'] as int,
        dayIndex: json['dayIndex'] as int,
        name: json['name'] as String?,
        restDay: json['restDay'] as bool? ?? false,
        exercises: (json['exercises'] as List<dynamic>? ?? const [])
            .map((e) => PlannedExercise.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}

class Workout {
  final int id;
  final int? userId;
  final String name;
  final String? description;
  final String? objective;
  final String? level;
  final int? durationWeeks;
  final bool template;
  final bool aiGenerated;
  final String status;
  final List<WorkoutDay> days;

  const Workout({
    required this.id,
    this.userId,
    required this.name,
    this.description,
    this.objective,
    this.level,
    this.durationWeeks,
    this.template = false,
    this.aiGenerated = false,
    this.status = 'active',
    this.days = const [],
  });

  factory Workout.fromJson(Map<String, dynamic> json) => Workout(
        id: json['id'] as int,
        userId: json['userId'] as int?,
        name: json['name'] as String,
        description: json['description'] as String?,
        objective: json['objective'] as String?,
        level: json['level'] as String?,
        durationWeeks: json['durationWeeks'] as int?,
        template: json['template'] as bool? ?? false,
        aiGenerated: json['aiGenerated'] as bool? ?? false,
        status: json['status'] as String? ?? 'active',
        days: (json['days'] as List<dynamic>? ?? const [])
            .map((e) => WorkoutDay.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
