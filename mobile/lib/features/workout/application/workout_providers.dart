// =====================================================================
// KineticOs — Providers del módulo workout (DI + lecturas + acciones).
// =====================================================================
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers/core_providers.dart';
import '../domain/exercise.dart';
import '../domain/exercise_filter.dart';
import '../domain/muscle.dart';
import '../domain/workout.dart';
import '../domain/workout_draft.dart';
import '../domain/workout_session.dart';
import '../infrastructure/workout_api.dart';

final workoutApiProvider = Provider<WorkoutApi>((ref) => WorkoutApi(ref.watch(apiClientProvider).dio));

// ------------------------- Catálogo -------------------------

final musclesProvider = FutureProvider<List<Muscle>>((ref) => ref.watch(workoutApiProvider).listMuscles());

final exerciseCatalogProvider = FutureProvider.family<List<Exercise>, ExerciseFilter>(
  (ref, filter) => ref.watch(workoutApiProvider).listExercises(filter),
);

final exerciseDetailProvider = FutureProvider.family<Exercise, int>(
  (ref, id) => ref.watch(workoutApiProvider).getExercise(id),
);

// ------------------------- Rutinas -------------------------

final workoutListProvider = FutureProvider.family<List<Workout>, bool>(
  (ref, templates) => ref.watch(workoutApiProvider).listWorkouts(templates: templates),
);

final workoutDetailProvider = FutureProvider.family<Workout, int>(
  (ref, id) => ref.watch(workoutApiProvider).getWorkout(id),
);

// ------------------------- Sesiones -------------------------

final sessionListProvider = FutureProvider<List<WorkoutSession>>(
  (ref) => ref.watch(workoutApiProvider).listSessions(),
);

final sessionDetailProvider = FutureProvider.family<WorkoutSession, int>(
  (ref, id) => ref.watch(workoutApiProvider).getSession(id),
);

// ------------------------- Acciones (mutaciones) -------------------------

final workoutActionsProvider = Provider<WorkoutActions>((ref) => WorkoutActions(ref, ref.watch(workoutApiProvider)));

class WorkoutActions {
  final Ref _ref;
  final WorkoutApi _api;

  WorkoutActions(this._ref, this._api);

  Future<Workout> createWorkout(WorkoutDraft draft) async {
    final created = await _api.createWorkout(draft);
    _invalidateLists();
    return created;
  }

  Future<Workout> updateWorkout(int id, WorkoutDraft draft) async {
    final updated = await _api.updateWorkout(id, draft);
    _invalidateLists();
    _ref.invalidate(workoutDetailProvider(id));
    return updated;
  }

  Future<void> deleteWorkout(int id) async {
    await _api.deleteWorkout(id);
    _invalidateLists();
  }

  void _invalidateLists() {
    _ref.invalidate(workoutListProvider(false));
    _ref.invalidate(workoutListProvider(true));
  }

  Future<WorkoutSession> startSession({int? workoutId, int? workoutDayId}) async {
    final session = await _api.startSession(workoutId: workoutId, workoutDayId: workoutDayId);
    _ref.invalidate(sessionListProvider);
    return session;
  }

  Future<WorkoutSession> completeSession(int id, {int? durationSeconds, String? notes}) async {
    final session = await _api.completeSession(id, durationSeconds: durationSeconds, notes: notes);
    _ref.invalidate(sessionListProvider);
    _ref.invalidate(sessionDetailProvider(id));
    return session;
  }

  Future<WorkoutSession> abortSession(int id, {String? notes}) async {
    final session = await _api.abortSession(id, notes: notes);
    _ref.invalidate(sessionListProvider);
    _ref.invalidate(sessionDetailProvider(id));
    return session;
  }

  Future<void> logSessionExercise(
    int sessionId, {
    int? workoutExerciseId,
    required int exerciseId,
    int? setsDone,
    double? weightKg,
    int? reps,
    int? rpe,
  }) async {
    await _api.logSessionExercise(sessionId,
        workoutExerciseId: workoutExerciseId,
        exerciseId: exerciseId,
        setsDone: setsDone,
        weightKg: weightKg,
        reps: reps,
        rpe: rpe);
    _ref.invalidate(sessionDetailProvider(sessionId));
  }
}
