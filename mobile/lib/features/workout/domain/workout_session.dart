// =====================================================================
// MiCoach — Sesión de entrenamiento (historial), mirror de WorkoutDtos.
// =====================================================================
class SessionExercise {
  final int id;
  final int? workoutExerciseId;
  final int exerciseId;
  final int? setsDone;
  final double? weightKg;
  final int? reps;
  final int? rpe;
  final int? durationSeconds;
  final int? distanceMeters;
  final String? notes;

  const SessionExercise({
    required this.id,
    this.workoutExerciseId,
    required this.exerciseId,
    this.setsDone,
    this.weightKg,
    this.reps,
    this.rpe,
    this.durationSeconds,
    this.distanceMeters,
    this.notes,
  });

  factory SessionExercise.fromJson(Map<String, dynamic> json) => SessionExercise(
        id: json['id'] as int,
        workoutExerciseId: json['workoutExerciseId'] as int?,
        exerciseId: json['exerciseId'] as int,
        setsDone: json['setsDone'] as int?,
        weightKg: (json['weightKg'] as num?)?.toDouble(),
        reps: json['reps'] as int?,
        rpe: json['rpe'] as int?,
        durationSeconds: json['durationSeconds'] as int?,
        distanceMeters: json['distanceMeters'] as int?,
        notes: json['notes'] as String?,
      );
}

class WorkoutSession {
  final int id;
  final int? workoutId;
  final int? workoutDayId;
  final String status;
  final DateTime? startedAt;
  final DateTime? completedAt;
  final int? durationSeconds;
  final String? notes;
  final List<SessionExercise> exercises;

  const WorkoutSession({
    required this.id,
    this.workoutId,
    this.workoutDayId,
    required this.status,
    this.startedAt,
    this.completedAt,
    this.durationSeconds,
    this.notes,
    this.exercises = const [],
  });

  factory WorkoutSession.fromJson(Map<String, dynamic> json) => WorkoutSession(
        id: json['id'] as int,
        workoutId: json['workoutId'] as int?,
        workoutDayId: json['workoutDayId'] as int?,
        status: json['status'] as String,
        startedAt: json['startedAt'] != null ? DateTime.parse(json['startedAt'] as String) : null,
        completedAt: json['completedAt'] != null ? DateTime.parse(json['completedAt'] as String) : null,
        durationSeconds: json['durationSeconds'] as int?,
        notes: json['notes'] as String?,
        exercises: (json['exercises'] as List<dynamic>? ?? const [])
            .map((e) => SessionExercise.fromJson(e as Map<String, dynamic>))
            .toList(),
      );
}
