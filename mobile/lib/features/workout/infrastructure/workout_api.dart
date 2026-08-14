// =====================================================================
// KineticOs — Cliente REST del módulo workout (/api/v1/workouts).
// =====================================================================
import 'package:dio/dio.dart';

import '../domain/exercise.dart';
import '../domain/exercise_filter.dart';
import '../domain/muscle.dart';
import '../domain/workout.dart';
import '../domain/workout_draft.dart';
import '../domain/workout_session.dart';

class WorkoutApi {
  final Dio _dio;

  WorkoutApi(this._dio);

  // ------------------------- Catálogo -------------------------

  Future<List<Muscle>> listMuscles() async {
    final response = await _dio.get('/workouts/muscles');
    return (response.data as List<dynamic>).map((e) => Muscle.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<List<Exercise>> listExercises(ExerciseFilter filter) async {
    final response = await _dio.get('/workouts/exercises', queryParameters: filter.toQueryParams());
    return (response.data as List<dynamic>).map((e) => Exercise.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<Exercise> getExercise(int id) async {
    final response = await _dio.get('/workouts/exercises/$id');
    return Exercise.fromJson(response.data as Map<String, dynamic>);
  }

  // ------------------------- Rutinas -------------------------

  Future<List<Workout>> listWorkouts({required bool templates}) async {
    final response = await _dio.get('/workouts', queryParameters: {'templates': templates});
    return (response.data as List<dynamic>).map((e) => Workout.fromJson(e as Map<String, dynamic>)).toList();
  }

  Future<Workout> getWorkout(int id) async {
    final response = await _dio.get('/workouts/$id');
    return Workout.fromJson(response.data as Map<String, dynamic>);
  }

  Future<Workout> createWorkout(WorkoutDraft draft) async {
    final response = await _dio.post('/workouts', data: draft.toJson());
    return Workout.fromJson(response.data as Map<String, dynamic>);
  }

  Future<Workout> updateWorkout(int id, WorkoutDraft draft) async {
    final response = await _dio.put('/workouts/$id', data: draft.toJson());
    return Workout.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteWorkout(int id) => _dio.delete('/workouts/$id');

  /// Genera una rutina con IA a partir de un pedido en lenguaje natural. Puede tardar
  /// hasta un par de minutos (proveedor local por CPU) — timeout largo a propósito.
  Future<Workout> generateWorkout(String goal) async {
    final response = await _dio.post(
      '/workouts/generate',
      data: {'goal': goal},
      options: Options(sendTimeout: const Duration(seconds: 10), receiveTimeout: const Duration(seconds: 180)),
    );
    return Workout.fromJson(response.data as Map<String, dynamic>);
  }

  // ------------------------- Sesiones -------------------------

  Future<List<WorkoutSession>> listSessions() async {
    final response = await _dio.get('/workouts/sessions');
    return (response.data as List<dynamic>)
        .map((e) => WorkoutSession.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<WorkoutSession> getSession(int id) async {
    final response = await _dio.get('/workouts/sessions/$id');
    return WorkoutSession.fromJson(response.data as Map<String, dynamic>);
  }

  Future<WorkoutSession> startSession({int? workoutId, int? workoutDayId}) async {
    final response = await _dio
        .post('/workouts/sessions', data: {'workoutId': workoutId, 'workoutDayId': workoutDayId});
    return WorkoutSession.fromJson(response.data as Map<String, dynamic>);
  }

  Future<WorkoutSession> completeSession(int id, {int? durationSeconds, String? notes}) async {
    final response = await _dio.put('/workouts/sessions/$id/complete',
        data: {'durationSeconds': durationSeconds, 'notes': notes});
    return WorkoutSession.fromJson(response.data as Map<String, dynamic>);
  }

  Future<WorkoutSession> abortSession(int id, {String? notes}) async {
    final response = await _dio.put('/workouts/sessions/$id/abort', data: {'notes': notes});
    return WorkoutSession.fromJson(response.data as Map<String, dynamic>);
  }

  Future<SessionExercise> logSessionExercise(
    int sessionId, {
    int? workoutExerciseId,
    required int exerciseId,
    int? setsDone,
    double? weightKg,
    int? reps,
    int? rpe,
    int? durationSeconds,
    int? distanceMeters,
    String? notes,
  }) async {
    final response = await _dio.post('/workouts/sessions/$sessionId/exercises', data: {
      'workoutExerciseId': workoutExerciseId,
      'exerciseId': exerciseId,
      'setsDone': setsDone,
      'weightKg': weightKg,
      'reps': reps,
      'rpe': rpe,
      'durationSeconds': durationSeconds,
      'distanceMeters': distanceMeters,
      'notes': notes,
    });
    return SessionExercise.fromJson(response.data as Map<String, dynamic>);
  }
}
