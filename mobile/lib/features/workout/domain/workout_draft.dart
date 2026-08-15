// =====================================================================
// MiCoach — Modelos mutables para crear/editar una rutina (formulario).
// Se serializan al formato de WorkoutRequest esperado por el backend.
// =====================================================================
import 'workout.dart';

class PlannedExerciseDraft {
  int exerciseId;
  String exerciseName;
  int sets;
  int? repsMin;
  int? repsMax;
  int? restSeconds;

  PlannedExerciseDraft({
    required this.exerciseId,
    required this.exerciseName,
    this.sets = 3,
    this.repsMin,
    this.repsMax,
    this.restSeconds,
  });

  factory PlannedExerciseDraft.fromExisting(PlannedExercise e, String exerciseName) => PlannedExerciseDraft(
        exerciseId: e.exerciseId,
        exerciseName: exerciseName,
        sets: e.sets ?? 3,
        repsMin: e.repsMin,
        repsMax: e.repsMax,
        restSeconds: e.restSeconds,
      );

  Map<String, dynamic> toJson(int orderIndex) => {
        'exerciseId': exerciseId,
        'orderIndex': orderIndex,
        'sets': sets,
        'repsMin': repsMin,
        'repsMax': repsMax,
        'restSeconds': restSeconds,
      };
}

class WorkoutDayDraft {
  int dayIndex;
  String? name;
  bool restDay;
  List<PlannedExerciseDraft> exercises;

  WorkoutDayDraft({required this.dayIndex, this.name, this.restDay = false, List<PlannedExerciseDraft>? exercises})
      : exercises = exercises ?? [];

  Map<String, dynamic> toJson() => {
        'dayIndex': dayIndex,
        'name': name,
        'restDay': restDay,
        'exercises': [for (var i = 0; i < exercises.length; i++) exercises[i].toJson(i + 1)],
      };
}

class WorkoutDraft {
  String name;
  String? description;
  String? objective;
  String? level;
  int? durationWeeks;
  List<WorkoutDayDraft> days;

  WorkoutDraft({
    this.name = '',
    this.description,
    this.objective,
    this.level,
    this.durationWeeks,
    List<WorkoutDayDraft>? days,
  }) : days = days ?? [WorkoutDayDraft(dayIndex: 1)];

  Map<String, dynamic> toJson() => {
        'name': name,
        'description': description,
        'objective': objective,
        'level': level,
        'durationWeeks': durationWeeks,
        'days': days.map((d) => d.toJson()).toList(),
      };
}
